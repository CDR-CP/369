package lab7

import org.apache.spark.SparkContext._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level

object App {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf().setAppName("Lab7")
    val sc = new SparkContext(conf)

    val salesRDD    = sc.textFile(args(0))
    val lineItemRDD = sc.textFile(args(1))
    val productRDD  = sc.textFile(args(2))
    val storeRDD    = sc.textFile(args(3))

    // productID -> price
    val products = productRDD.map(_.split(",", 3))
      .filter(_.length >= 3)
      .map(p => (p(0).trim.toInt, p(2).trim.replace("$", "").toDouble))

    // storeID -> (name, city)
    val stores = storeRDD.map(_.split(","))
      .filter(_.length >= 4)
      .map(p => (p(0).trim.toInt, (p(1).trim, p(3).trim)))

    // saleID -> (month, storeID)  e.g. month = "2016-12"
    val sales = salesRDD.map(_.split(","))
      .filter(_.length >= 4)
      .map(p => {
        val date    = p(1).trim.replace("/", "-")
        val month   = date.substring(0, 7)
        val saleId  = p(0).trim.toInt
        val storeId = p(3).trim.toInt
        (saleId, (month, storeId))
      })

    // saleID -> (productID, quantity)
    val lineItems = lineItemRDD.map(_.split(","))
      .filter(_.length >= 4)
      .map(p => (p(1).trim.toInt, (p(2).trim.toInt, p(3).trim.toInt)))

    // join lineItems with products to get saleID -> cost
    val itemCosts = lineItems.join(products.map(p => (p._1, p._2)))
      .map({ case (saleId, ((productId, qty), price)) => (saleId, price * qty) })
      .reduceByKey(_ + _)

    // join with sales to get (month, storeID) -> total
    val byMonth = itemCosts.join(sales)
      .map({ case (saleId, (cost, (month, storeId))) => ((month, storeId), cost) })
      .reduceByKey(_ + _)

    // join with stores to get (month, storeID) -> (name, city, total)
    val withInfo = byMonth
      .map({ case ((month, storeId), total) => (storeId, (month, total)) })
      .join(stores)
      .map({ case (storeId, ((month, total), (name, city))) => (month, (name, city, total)) })

    // group by month, sort each group by total descending, take top 10 (for each month!!!)
    val result = withInfo.groupByKey()
      .sortByKey()
      .map({ case (month, storeList) =>
        val top10 = storeList.toList.sortWith((a, b) => a._3 > b._3).take(10)
        val out = top10.map({ case (name, city, total) =>
          f"($name, $city, $$${total}%.0f)"
        }).mkString(", ")
        s"$month $out"
      })

    result.saveAsTextFile(args(4))
    sc.stop()
  }
}

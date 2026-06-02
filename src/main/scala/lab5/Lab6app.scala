package lab6

import org.apache.spark.SparkContext._
import scala.io._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level

object App {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val conf = new SparkConf().setAppName("Lab6")
    val sc = new SparkContext(conf)

    val salesRDD = sc.textFile(args(0))
    val lineItemRDD = sc.textFile(args(1))
    val productRDD = sc.textFile(args(2))
    val storeRDD = sc.textFile(args(3))

    // productID -> price
    val products = productRDD.map(line => line.split(",", 3))
      .filter(_.length >= 3)
      .map(p => (p(0).trim.toInt, p(2).trim.replace("$", "").toDouble))

    // storeID -> state
    val stores = storeRDD.map(line => line.split(","))
      .filter(_.length >= 6)
      .map(p => (p(0).trim.toInt, p(5).trim))

    // saleID -> storeID
    val sales = salesRDD.map(line => line.split(","))
      .filter(_.length >= 4)
      .map(p => (p(0).trim.toInt, p(3).trim.toInt))

    // saleID -> (productID, quantity)
    val lineItems = lineItemRDD.map(line => line.split(","))
      .filter(_.length >= 4)
      .map(p => (p(1).trim.toInt, (p(2).trim.toInt, p(3).trim.toInt)))

    // join lineItems with sales, rekey by productID
    val withStore = lineItems.join(sales)
      .map({ case (saleId, ((productId, qty), storeId)) => (productId, (storeId, qty)) })

    // join w/ products to get costper line item, rekey by storeID
    val withPrice = withStore.join(products)
      .map({ case (productId, ((storeId, qty), price)) => (storeId, price * qty) })

    // sum up totals per store then join with stores to get state
    val storeTotals = withPrice.reduceByKey(_ + _)
    val result = storeTotals.join(stores)
      .map({ case (storeId, (total, state)) => (state, storeId, total) })
      .sortBy(r => r._1)

    result.map({ case (state, storeId, total) =>
      f"$state, $storeId, $total%.2f"
    }).saveAsTextFile(args(4))

    sc.stop()
  }
}

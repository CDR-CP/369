package lab5

import scala.io._
import java.io._
import scala.collection.mutable._

object App {
  def main(args: Array[String]): Unit = {

    if (args.length != 5) {
      println("Usage: App <sales> <lineItems> <products> <stores> <output>")
      return
    }

    val salesFile    = args(0)
    val lineItemFile = args(1)
    val productFile  = args(2)
    val storeFile    = args(3)
    val outputFile   = args(4)

    // load products into a hashmap so we can look up price by product id quickly
    val productPrices = new HashMap[Int, Double]()
    for (line <- Source.fromFile(productFile).getLines) {
      val parts = line.split(",", 3)
      if (parts.length >= 3) {
        val id    = parts(0).trim.toInt
        val price = parts(2).trim.replace("$", "").toDouble
        productPrices += (id -> price)
      }
    }

    // map store id to its state, need this for sorting output later
    val storeState = new HashMap[Int, String]()
    for (line <- Source.fromFile(storeFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 6) {
        val id    = parts(0).trim.toInt
        val state = parts(5).trim
        storeState += (id -> state)
      }
    }

    // map sale id to store id so we know which store made each sale
    val saleStore = new HashMap[Int, Int]()
    for (line <- Source.fromFile(salesFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 4) {
        val saleId  = parts(0).trim.toInt
        val storeId = parts(3).trim.toInt
        saleStore += (saleId -> storeId)
      }
    }

    // go through line items and accumulate total sales per store
    // multiply quantity by price and add to the stores running total
    val storeTotals = new HashMap[Int, Double]()
    for (line <- Source.fromFile(lineItemFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 4) {
        val saleId    = parts(1).trim.toInt
        val productId = parts(2).trim.toInt
        val quantity  = parts(3).trim.toInt
        if (saleStore.contains(saleId) && productPrices.contains(productId)) {
          val storeId = saleStore(saleId)
          val total   = productPrices(productId) * quantity
          storeTotals(storeId) = storeTotals.getOrElse(storeId, 0.0) + total
        }
      }
    }

    // build a list of tuples (state, storeId, total) and sort by state
    val results = storeTotals.keys.toList
      .filter(storeState.contains)
      .map(storeId => (storeState(storeId), storeId, storeTotals(storeId)))
      .sortWith((a, b) => a._1 < b._1)

    // format each result as a string then join with newlines using mkString
    val lines = results.map(r => f"${r._1}, ${r._2}, ${r._3}%.2f")
    val pw = new PrintWriter(new File(outputFile))
    pw.write(lines.mkString("\r\n"))
    pw.close

    println("Output written to " + outputFile)
  }
}

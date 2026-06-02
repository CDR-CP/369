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

    val salesFile = args(0)
    val lineItemFile = args(1)
    val productFile = args(2)
    val storeFile = args(3)
    val outputFile = args(4)

    // productID -> price
    val productPrices = new HashMap[Int, Double]()
    for (line <- Source.fromFile(productFile).getLines) {
      val parts = line.split(",", 3)
      if (parts.length >= 3) {
        productPrices += (parts(0).trim.toInt -> parts(2).trim.replace("$", "").toDouble)
      }
    }

    // storeID -> state
    val storeState = new HashMap[Int, String]()
    for (line <- Source.fromFile(storeFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 6)
        storeState += (parts(0).trim.toInt -> parts(5).trim)
    }

    // saleID -> storeID
    val saleStore = new HashMap[Int, Int]()
    for (line <- Source.fromFile(salesFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 4)
        saleStore += (parts(0).trim.toInt -> parts(3).trim.toInt)
    }

    // accumulate total sales per store using line items
    val storeTotals = new HashMap[Int, Double]()
    for (line <- Source.fromFile(lineItemFile).getLines) {
      val parts = line.split(",")
      if (parts.length >= 4) {
        val saleId = parts(1).trim.toInt
        val productId = parts(2).trim.toInt
        val quantity = parts(3).trim.toInt
        if (saleStore.contains(saleId) && productPrices.contains(productId)) {
          val storeId = saleStore(saleId)
          storeTotals(storeId) = storeTotals.getOrElse(storeId, 0.0) + productPrices(productId) * quantity
        }
      }
    }

    val results = storeTotals.map({ case (storeId, total) =>
      (storeState.getOrElse(storeId, ""), storeId, total)
    }).filter({ case (state, _, _) => state != "" })
      .toList
      .sortWith((a, b) => a._1 < b._1)

    val lines = results.map({ case (state, storeId, total) =>
      f"$state, $storeId, $total%.2f"
    })

    val pw = new PrintWriter(new File(outputFile))
    pw.write(lines.mkString("\r\n"))
    pw.close

    println("Output written to " + outputFile)
  }
}

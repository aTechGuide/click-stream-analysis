package guide.atech

import guide.atech.analysis.Aggregator
import guide.atech.consumer.KafkaConsumer
import org.apache.spark.sql.SparkSession

object ClickStreamApp {

  def main(args: Array[String]): Unit = {

    implicit val spark = createSparkSession

    val consumer = new KafkaConsumer
    val responseDS = consumer.readUserResponses

    // consumer.logUserResponses(responseDS)

    Aggregator.computeAverageResponseTime(10, responseDS)

  }

  private def createSparkSession = {

    val spark = SparkSession.builder()
      .appName("Click Stream App")
      .master("local[2]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
    spark
  }

}

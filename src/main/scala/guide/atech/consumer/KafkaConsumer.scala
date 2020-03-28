package guide.atech.consumer

import org.apache.spark.sql.{Dataset, SparkSession}

case class UserResponse(sessionId: String, clickDuration: Long)
class KafkaConsumer {

  def readUserResponses(implicit spark: SparkSession): Dataset[UserResponse] = {

    import spark.implicits._

    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "atechguide_click_stream")
      .load()
      .select("value")
      .as[String]
      .map { line =>
        val tokens = line.split(",")
        UserResponse(tokens(0), tokens(1).toLong)
      }
  }

  def logUserResponses(responseDS: Dataset[UserResponse]): Unit = {
    responseDS.writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()
  }

}

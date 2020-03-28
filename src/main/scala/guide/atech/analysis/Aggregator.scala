package guide.atech.analysis

import guide.atech.consumer.UserResponse
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.streaming.{GroupState, GroupStateTimeout, OutputMode}

case class UserAvgResponse(sessionId: String, avgDuration: Double)
object Aggregator {

  /*
    ROLLING WINDOW BY ELEMENTS
   Aggregate the ROLLING average response time over the past 3 clicks


   uurt("abc", [100, 200, 300, 400, 500, 600], Empty) => Iterator(200, 300, 400, 500)

   100 -> state becomes [100]
   200 -> state becomes [100, 200]
   300 -> state becomes [100, 200, 300] -> first average 200
   400 -> state becomes [200, 300, 400] -> next average 300
   500 -> state becomes [300, 400, 500] -> next average 400
   600 -> state becomes [400, 500, 600] -> next average 500

   Iterator will contain 200, 300, 400, 500

   Real time:
    61159462-0bb4-42b1-aa4b-ac242b3444a0,1186
    61159462-0bb4-42b1-aa4b-ac242b3444a0,615
    61159462-0bb4-42b1-aa4b-ac242b3444a0,1497
    61159462-0bb4-42b1-aa4b-ac242b3444a0,542
    61159462-0bb4-42b1-aa4b-ac242b3444a0,720

    window 1 = [1186, 615, 1497] = 1099.3
    window 2 = [615, 1497, 542] = 884.6
    window 3 = [1497, 542, 720] = 919.6

    next batch

    61159462-0bb4-42b1-aa4b-ac242b3444a0,768
    61159462-0bb4-42b1-aa4b-ac242b3444a0,583
    61159462-0bb4-42b1-aa4b-ac242b3444a0,485
    61159462-0bb4-42b1-aa4b-ac242b3444a0,469
    61159462-0bb4-42b1-aa4b-ac242b3444a0,566
    61159462-0bb4-42b1-aa4b-ac242b3444a0,486

    window 4 = [542, 720, 768] = 676.6
*/

  def updateUserResponseTime
  (n: Int)
  (sessionId: String, group: Iterator[UserResponse], state: GroupState[List[UserResponse]])
  : Iterator[UserAvgResponse] = {

    group.flatMap{ record =>
      val lastWindow =
        if (state.exists) state.get
        else List()

      val windowLength = lastWindow.length
      val newWindow =
        if (windowLength >= n) lastWindow.tail :+ record
        else lastWindow :+ record

      // for spark to give us access to the state in the next batch
      state.update(newWindow)

      if (newWindow.length >= n) {
        val newAverage = newWindow.map(_.clickDuration).sum * 1.0 / n
        Iterator(UserAvgResponse(sessionId, newAverage))
      } else {
        Iterator()
      }

    }
  }

  def computeAverageResponseTime(n: Int, stream: Dataset[UserResponse])(implicit spark: SparkSession): Unit = {

    import spark.implicits._

    stream
      .groupByKey(_.sessionId)
      .flatMapGroupsWithState(OutputMode.Append(), GroupStateTimeout.NoTimeout())(updateUserResponseTime(n))
      .writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()

  }


}

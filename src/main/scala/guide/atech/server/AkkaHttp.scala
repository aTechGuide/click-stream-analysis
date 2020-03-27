package guide.atech.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.io.Source

case class AkkaHttp() {

  val kafkaTopic = "atechguide_click_stream"
  val kafkaBootstrapServer = "localhost:9092"

  def getRoute(producer: KafkaProducer[Long, String]): Route = {
    pathEndOrSingleSlash {
      get {
        complete(
          HttpEntity (
            ContentTypes.`text/html(UTF-8)`,
            Source.fromFile("src/main/html/whackamole.html").getLines().mkString("")
          )
        )
      }
    } ~
    path("api" / "report") {
      (parameter("sessionId".as[String]) & parameter("time".as[Long])) { (sessionId: String, time: Long ) =>
        println(s"I've found session ID $sessionId and time = $time ")
        // create a record to send to Kafka
        val record = new ProducerRecord[Long, String](kafkaTopic, 0, s"$sessionId,$time")
        producer.send(record)
        producer.flush()

        complete(StatusCodes.OK)

      }
    }
  }
}

object AkkaHttp {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
}

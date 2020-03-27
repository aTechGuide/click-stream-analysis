package guide.atech

import akka.http.scaladsl.Http
import guide.atech.producer.Producer
import guide.atech.server.AkkaHttp

object ServerApp {

  def main(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global
    import AkkaHttp._

    // Spinning up the server
    val kafkaProducer = Producer().getProducer
    val bindingFuture = Http().bindAndHandle(AkkaHttp()
      .getRoute(kafkaProducer), "localhost", 9988)

    // clean up
    bindingFuture.foreach {binding =>
      binding.whenTerminated.onComplete(_ => kafkaProducer.close())
    }

  }

}

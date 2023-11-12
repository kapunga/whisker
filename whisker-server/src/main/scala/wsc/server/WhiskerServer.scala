package wsc.server

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}

object WhiskerServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    runServer
  }
  private def runServer: IO[Nothing] = {
    val httpApp = Router("/whisker" -> connectService).orNotFound
    for {
      _ <-
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    } yield ()
  }.useForever
}

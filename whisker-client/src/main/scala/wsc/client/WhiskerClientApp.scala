package wsc.client

import cats.effect.*
import cats.effect.kernel.Resource
import cats.effect.std.Console
import fs2.io.net.Network
import fs2.io.net.tls.S2nConfig
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.Uri
import org.http4s.client.websocket.WSClient

object WhiskerClientApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    httpClient.use { clients =>
      for {
        args <- parseArgs(args)
        (client, wsClient) = clients
        _ <- Console[IO].println(s"wsClient -> ${wsClient.getClass.getName}")
        whiskerClient = WhiskerClient(client)
        wsUri <- whiskerClient.connect(args.connectUri)
        _ <- WhiskerConnection(wsUri, wsClient).run()
      } yield ExitCode.Success
    }

  private def parseArgs(args: List[String]): IO[CmdLineArgs] = {
    args.headOption.map(uri => {
      val either = Uri.fromString(uri)
        .map(CmdLineArgs.apply)
        .left.map(e => new RuntimeException(s"Bad command line args", e))

      IO.fromEither(either)

    }).getOrElse(IO.raiseError(new RuntimeException("Missing url argument.")))
  }
}

case class CmdLineArgs(connectUri: Uri)

def httpClient: Resource[IO, (Client[IO], WSClient[IO])] = {
  val tls = S2nConfig.builder
    .withCipherPreferences("default_tls13")
    .build[IO]
    .map(Network[IO].tlsContext.fromS2nConfig(_))

  tls.flatMap { tlsContext =>
    EmberClientBuilder
      .default[IO]
      .withTLSContext(tlsContext)
      // TODO - Websockets are getting auto-upgraded to http2. Websockets can't be negotiated
      //        over http2, so this line has to stay out until the bug in ember-client is fixed.
      //.withHttp2
      .buildWebSocket
  }
}

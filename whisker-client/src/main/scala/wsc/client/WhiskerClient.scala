package wsc.client

import cats.effect.IO
import cats.effect.std.Console
import org.http4s.*
import org.http4s.client.Client
import org.http4s.headers.{Accept, Authorization}
import wsc.core.{RegistrationPath, ServerWelcome}

class WhiskerClient(client: Client[IO]) {
  def connect(connectUri: Uri): IO[Uri] =
    for {
      serverInfo <- getServerStats(connectUri)
      wsUri      <- login(serverInfo)
      _          <- Console[IO].println(s"Websocket URI - $wsUri")
    } yield wsUri

  private def login(serverInfo: ServerInfo): IO[Uri] =
    for {
      _         <- Console[IO].println(serverInfo.welcome.map(_.msg).getOrElse(defaultWelcome(serverInfo.connectUri)))
      _         <- Console[IO].print("Username: ")
      username  <- Console[IO].readLine
      _         <- Console[IO].print("Password: ")
      password  <- Console[IO].readLine
      socketUri <- getWebsocketUri(username, password, serverInfo.connectUri)
    } yield socketUri

  private def defaultWelcome(connectUri: Uri): String = s"Connecting to host: $connectUri"

  private def getServerStats(connectUri: Uri): IO[ServerInfo] =
    client.get[ServerInfo](connectUri) { resp =>
      if (resp.status == Status.Unauthorized) {
        val welcome = resp.headers.get[ServerWelcome]
        val regPath = resp.headers.get[RegistrationPath]

        IO.pure(ServerInfo(connectUri, welcome, regPath))
      } else {
        IO.pure(ServerInfo(connectUri, None, None))
      }
    }

  private def getWebsocketUri(username: String, password: String, connectUri: Uri): IO[Uri] =
    val request = Request[IO](
      method = Method.GET,
      uri = connectUri,
      headers = Headers(
        Authorization(BasicCredentials(username, password)),
        Accept(MediaType.text.plain)
      )
    )

    client.expect[String](request)
      .flatMap(body => Uri.fromString(body)
        .fold(pf => IO.raiseError(new RuntimeException(s"$pf")),  IO.pure))
}

case class ServerInfo(connectUri: Uri, welcome: Option[ServerWelcome], registrationPath: Option[RegistrationPath])

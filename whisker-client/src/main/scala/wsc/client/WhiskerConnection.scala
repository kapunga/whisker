package wsc.client

import cats.effect.{IO, Resource}
import cats.effect.std.Console
import org.http4s.Uri
import org.http4s.client.websocket.{WSClient, WSConnectionHighLevel, WSFrame, WSRequest}

class WhiskerConnection(wsConn: Resource[IO,  WSConnectionHighLevel[IO]]) {
  def run(): IO[Unit] = wsConn.use { conn =>
    conn.send(WSFrame.Text("Hello from ember websockets")) *>
      conn.receive.flatMap(Console[IO].println(_))
  }
}

object WhiskerConnection {
  def apply(uri: Uri, wsClient: WSClient[IO]): WhiskerConnection = {
    val wsConnection = wsClient.connectHighLevel(WSRequest(uri))

    new WhiskerConnection(wsConnection)
  }
}

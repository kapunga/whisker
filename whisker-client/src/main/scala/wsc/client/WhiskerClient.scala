package wsc.client

import cats.effect.*
import epollcat.EpollApp

object WhiskerClient extends EpollApp {
  override def run(args: List[String]): IO[ExitCode] = IO.stub
}

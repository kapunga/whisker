package wsc.server

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.std.Console
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.{Authorization, `WWW-Authenticate`}
import org.http4s.server.*
import wsc.core.*

case class AuthedUser(userName: String, host: String)

val authUser: Kleisli[OptionT[IO, *], Request[IO], AuthedUser] =
  Kleisli(req => OptionT(lookupUser(req)))

def lookupUser(request: Request[IO]): IO[Option[AuthedUser]] = {
  def userFromToken(token: String): Option[AuthedUser] = {
    val decoder = java.util.Base64.getDecoder
    val decodedToken = String(decoder.decode(token.getBytes))

    decodedToken.split(":") match {
      case Array(userName, _) =>
        val host = request.uri.host.map(_.value).getOrElse("localhost")
        Option(AuthedUser(userName, host))
      case _ => None
    }
  }

  val authedUser =
    for {
      authHeader <- request.headers.get[Authorization]
      credentials = authHeader.credentials
      token <- credentials match {
        case Credentials.Token(AuthScheme.Basic, t) => Some(t)
        case _ => None
      }
      user <- userFromToken(token)
    } yield user

  IO.pure(authedUser)
}

val authMiddleware: AuthMiddleware[IO, AuthedUser] =
  AuthMiddleware(authUser)

val authedRoutes = AuthedRoutes.of[AuthedUser, IO] {
  case GET -> Root / "connect" as authedUser =>
    Ok(wsForUser(authedUser))
}

def wsForUser(user: AuthedUser): IO[String] =
  for {
    _ <- Console[IO].println(s"$user logged in!")
  } yield "wss://ws.postman-echo.com/raw"

def unauthorizedHeaders(response: Response[IO]): Response[IO] = {
  response match {
    case Status.Unauthorized(resp) =>
      resp.putHeaders(
        ServerWelcome("Welcome to the new WhiskerServer 0.1!"),
        RegistrationPath("register"),
        `WWW-Authenticate`(Challenge("Basic", "WebSocket Generation")))
    case resp => resp
  }
}

val connectService: HttpRoutes[IO] =
  authMiddleware(authedRoutes).map(unauthorizedHeaders)

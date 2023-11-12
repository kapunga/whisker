package wsc.core

import org.http4s.{Header, ParseFailure}
import org.typelevel.ci.*

/**
 * If a whisker server has a custom welcome message, it should be
 * returned with this header on a 401 response to the login path.
 */
case class RegistrationPath(path: String)

object RegistrationPath {
  given Header[RegistrationPath, Header.Single] with {
    def name: CIString = ci"X-Registration-Path"
    def value(registrationPath: RegistrationPath): String = registrationPath.path
    def parse(headerValue: String): Either[ParseFailure, RegistrationPath] =
      Right(RegistrationPath(headerValue))
  }
}

/**
 * If a whisker server has a custom welcome message, it should be
 * returned with this header on a 401 response to the login path.
 */
case class ServerWelcome(msg: String)

object ServerWelcome {
  given Header[ServerWelcome, Header.Single] with {
    def name: CIString = ci"X-Server-Welcome"
    def value(serverWelcome: ServerWelcome): String = serverWelcome.msg
    def parse(headerValue: String): Either[ParseFailure, ServerWelcome] =
      Right(ServerWelcome(headerValue))
  }
}

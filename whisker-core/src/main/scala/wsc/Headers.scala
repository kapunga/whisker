package wsc

object Headers {
  /**
   * If a whisker server has a path to register a new account, this
   * header should be returned with the 401 response on the login path.
   */
  val RegistrationPath = "X-Registration-Path"

  /**
   * If a whisker server has a custom welcome message, it should be
   * returned with this header on a 401 response to the login path.
   */
  val ServerWelcome = "X-Server-Welcome"
}

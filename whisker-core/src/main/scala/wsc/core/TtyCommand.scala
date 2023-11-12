package wsc.core

sealed trait TtyCommand {
  def render: String
  override def toString: String = render
}

case object Bell extends TtyCommand {
  override def render: String = "\u0007"
}

sealed trait Color extends TtyCommand {
  def text: String
  def code: String
  def render: String = s"\u001B[0;${code}m$text\u001B[m"
}

case class Red(text: String) extends Color { val code = "31" }
case class Magenta(text: String) extends Color { val code = "35"}
case class Blue(text: String) extends Color { val code = "34" }
case class Green(text: String) extends Color { val code = "32" }
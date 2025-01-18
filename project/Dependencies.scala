import sbt.*

object Dependencies {
  object V {
    val terminus = "0.3"
  }

  object Libraries {
    val terminus: Seq[ModuleID] =
      Seq("org.creativescala" %% "terminus-core" % V.terminus)
  }
}

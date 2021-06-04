package sbtstudent

import sbt._
import Keys._

object AdditionalSettings {

  // Change 'loadInitialCmds' to true when requested in exercise instructions
  val loadInitialCmds = false

  val initialCmdsConsole: Seq[Def.Setting[String]] =
    if (loadInitialCmds) {
//      Seq(initialCommands in console := "import com.lightbend.training.scalatrain._")
      Seq()
    } else {
      Seq()
    }

  val initialCmdsTestConsole: Seq[Def.Setting[String]]  =
    if (loadInitialCmds) {
      Seq(Test /console / initialCommands := (console / initialCommands).value + ", TestData._")
    } else {
      Seq()
    }

  // Note that if no command aliases need to be added, assign an empty Seq to cmdAliasesIn
  val cmdAliasesIn: Seq[Def.Setting[(State) => State]] = Seq(
    //    addCommandAlias("xxx", "help"),
    //    addCommandAlias("yxy", "help")
  ).flatten

  val cmdAliases: Seq[Def.Setting[(State) => State]] =
    cmdAliasesIn
}

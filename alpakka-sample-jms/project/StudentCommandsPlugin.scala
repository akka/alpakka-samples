package sbtstudent

import sbt.Keys._
import sbt.{Def, _}
import stbstudent.MPSelection

import scala.Console

object StudentCommandsPlugin extends AutoPlugin {

  override val requires = sbt.plugins.JvmPlugin
  override val trigger: PluginTrigger = allRequirements
  object autoImport {
  }
  override lazy val globalSettings =
    Seq(
      commands in Global ++=
        Seq(
          Man.man, MPSelection.activateAllExercises, MPSelection.setActiveExerciseNr
        ),
      onLoad in Global := {
        val state = (onLoad in Global).value
        Navigation.loadBookmark compose(Navigation.setupNavAttrs compose state)
      }
    )

  def extractCurrentExerciseDesc(state: State): String = {
    val currentExercise =  Project.extract(state).currentProject.id

    currentExercise
      .replaceFirst("""^.*_\d{3}_""", "")
      .replaceAll("_", " ")
  }

  def extractProjectName(state: State): String = {
    IO.readLines(new sbt.File(new sbt.File(Project.extract(state).structure.root), ".courseName")).head
  }

  override lazy val projectSettings: Seq[Def.Setting[State => String]] =
    Seq(
      shellPrompt := { state =>
        val exercise = Console.GREEN + extractCurrentExerciseDesc(state) + Console.RESET
        val manRmnd = Console.GREEN + "man [e]" + Console.RESET
        val prjNbrNme = extractProjectName(state)
        s"$manRmnd > $prjNbrNme > $exercise > "
      }
    )
}

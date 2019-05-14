package sbtstudent

/**
  * Copyright © 2014, 2015, 2016 Lightbend, Inc. All rights reserved. [http://www.typesafe.com]
  */

import sbt._
import scala.Console

object Navigation {

  val setupNavAttrs: (State) => State = (state: State) => state

  val loadBookmark: (State) => State = (state: State) => {
    // loadBookmark doesn't really load a bookmark for a master repo.
    // It just selects the first exercise (project) from the repo
    val refs =
    Project.extract(state)
      .structure
      .allProjectRefs
      .toList
      .map(r => r.project)
      // By convention, a project exercise has a 3-digit number in it enclosed in underscores
      .filter(_.matches(""".*_\d{3}_.*"""))
      .sorted
    if (refs.nonEmpty)
      Command.process(s"project ${refs.head}", state)
    else {
      // No project was found adhering to the naming convention
      println(s"\n${Console.RED}No projects found!${Console.RESET}\n")
      state
    }
  }
}

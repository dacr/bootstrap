import AssemblyKeys._

seq(assemblySettings: _*)

name := "bootstrap"

version := "0.1.2"

organization :="fr.janalyse"

organizationHomepage := Some(new URL("http://www.janalyse.fr"))

scalaVersion := "2.9.2"

crossScalaVersions := Seq("2.8.1", "2.8.2", "2.9.1", "2.9.2")

//libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
//  val versionMap = Map("2.8.1" -> "1.5.1",   "2.8.2" -> "1.5.1",   "2.9.1" -> "1.6.1")
//  val testVersion = versionMap.getOrElse(sv, error("Unsupported Scala version " + sv))
//  deps :+ ("org.scalatest" %% "scalatest" % testVersion % "test")
//}

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

libraryDependencies <++=  scalaVersion { sv =>
   ("org.scala-lang" % "scala-swing" % sv) ::
   ("org.scala-lang" % "jline"           % sv  % "compile") ::
   ("org.scala-lang" % "scala-compiler"  % sv  % "compile") ::
   ("org.scala-lang" % "scala-dbc"       % sv  % "compile") ::
   ("org.scala-lang" % "scalap"          % sv  % "compile") ::
   ("org.scala-lang" % "scala-swing"     % sv  % "compile") ::Nil   
}

libraryDependencies += "junit" % "junit" % "4.10" % "test"


mainClass in assembly := Some("fr.janalyse.script.Bootstrap")

jarName in assembly := "bootstrap.jar"

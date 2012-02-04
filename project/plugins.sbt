// Eclipse IDE plugin for SBT - Allowing sbt project import/editing within eclipse
// but keeping SBT as the main build system

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0-M3")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.7.2")

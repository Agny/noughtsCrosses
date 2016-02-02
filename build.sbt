enablePlugins(ScalaJSPlugin)

name := "zeroCross"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "amateras-repo" at "http://amateras.sourceforge.jp/mvn-snapshot/"

libraryDependencies ++= Seq(
  "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
  "com.scalawarrior" %%% "scalajs-createjs" % "0.0.1-SNAPSHOT",
  "org.scalafx" %% "scalafx" % "8.0.60-R9",
  "com.typesafe" % "config" % "1.3.0"
)

skip in packageJSDependencies := false
persistLauncher in Compile := true
enablePlugins(ScalaJSPlugin)

name := "zeroCross"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "amateras-repo" at "http://amateras.sourceforge.jp/mvn-snapshot/"

libraryDependencies ++= Seq(
  "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
  "com.scalawarrior" %%% "scalajs-createjs" % "0.0.1-SNAPSHOT"
)

skip in packageJSDependencies := false
persistLauncher in Compile := true

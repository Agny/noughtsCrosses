enablePlugins(JettyPlugin)

name := "zeroCross"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.60-R9",
  "com.typesafe" % "config" % "1.3.0",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.0.3",
  "io.spray" %%  "spray-json" % "1.3.2"
)
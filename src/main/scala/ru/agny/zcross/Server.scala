package ru.agny.zcross

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import ru.agny.zcross.services.{GameService, IndexService}
import ru.agny.zcross.utils.PropertiesHolder

import scala.io.StdIn

object Server extends App {

  implicit val actorSystem = ActorSystem("akka-system")
  implicit val flowMaterializer = ActorMaterializer()

  val httpHost = PropertiesHolder.httpHost
  val httpPort = PropertiesHolder.httpPort
  val wsHost = PropertiesHolder.wsHost
  val wsPort = PropertiesHolder.wsPort

  val bindings = List(
    Http().bindAndHandle(IndexService.route, httpHost, httpPort),
    Http().bindAndHandle(GameService.route, wsHost, wsPort)
  )
  println(s"Server is now online\nPress any key to stop...")
  StdIn.readLine()

  import actorSystem.dispatcher

  bindings.foreach(_.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate()))
  println("Server is down...")

}
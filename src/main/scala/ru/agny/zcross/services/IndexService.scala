package ru.agny.zcross.services

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.MediaTypes.`application/json`
import ru.agny.zcross.utils.{JsonHandler, PropertiesHolder}
import spray.json._

object IndexService {

  private val gameConfig = PropertiesHolder.gameConfig
  private implicit val gameConfigFormat = JsonHandler.gameConfig

  def route: Route =
    pathSingleSlash {
      val index = "index.html"
      get {
        getFromResource(index)
      }
    } ~ pathPrefix("config.json") {
      get {
        complete {
          HttpEntity(`application/json`, gameConfig.toJson.compactPrint)
        }
      }
    }
}
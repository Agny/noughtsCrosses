package ru.agny.zcross.utils

import com.typesafe.config.ConfigFactory

object PropertiesHolder {

  private val conf = ConfigFactory.load("application.conf")
  conf.checkValid(ConfigFactory.defaultReference)

  lazy val gameConfig: GameConfig = GameConfig(
    conf.getInt("game.borderSide"),
    conf.getInt("game.boardSize"),
    conf.getInt("game.lineSize"),
    conf.getString("ws.host"),
    conf.getInt("ws.port")
  )

  lazy val httpHost = conf.getString("server.host")
  lazy val httpPort = conf.getInt("server.port")
  lazy val wsHost = conf.getString("ws.host")
  lazy val wsPort = conf.getInt("ws.port")

}

case class GameConfig(borderSide: Int, boardSize: Int, lineSize: Int, host:String, port:Int)

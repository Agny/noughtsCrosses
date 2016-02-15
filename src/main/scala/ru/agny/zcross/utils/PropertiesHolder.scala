package ru.agny.zcross.utils

import com.typesafe.config.ConfigFactory

object PropertiesHolder {

  val (borderSide: Int, boardSize: Int, lineSize: Int) = {
    val conf = ConfigFactory.load("application.conf")
    (
      conf.getInt("game.borderSide"),
      conf.getInt("game.boardSize"),
      conf.getInt("game.lineSize")
      )
  }
}

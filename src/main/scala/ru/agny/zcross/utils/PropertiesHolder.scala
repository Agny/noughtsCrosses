package ru.agny.zcross.utils

import com.typesafe.config.ConfigFactory

object PropertiesHolder {
  val (boardSize: Int, lineSize: Int) = {
    val conf = ConfigFactory.load("app.conf")
    (
      conf.getInt("boardSize"),
      conf.getInt("lineSize")
    )
  }
}

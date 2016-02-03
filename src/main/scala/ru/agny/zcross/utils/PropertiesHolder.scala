package ru.agny.zcross.utils

import com.typesafe.config.ConfigFactory

object PropertiesHolder {

  /* Scala.js doesn't support typesafe ConfigFactory yet

  val (boardSize: Int, lineSize: Int) = {
    val conf = ConfigFactory.load("app.conf")
    (
      conf.getInt("boardSize"),
      conf.getInt("lineSize")
    )
  }*/

  val borderSide = 600
  val boardSize = 5
  val lineSize = 4
}

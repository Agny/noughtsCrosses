package ru.agny.zcross.utils

import ru.agny.zcross.engine.messages.{GameOver, CellLine, CellClick, CellClickResult}
import spray.json.DefaultJsonProtocol

object JsonHandler extends DefaultJsonProtocol {
  implicit val cellClick = jsonFormat3(CellClick)
  implicit val cellClickResult = jsonFormat3(CellClickResult)
  implicit val cellLine = jsonFormat2(CellLine)
  implicit val gameOver = jsonFormat2(GameOver)
}

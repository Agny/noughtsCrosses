package ru.agny.zcross.utils

import ru.agny.zcross.engine.messages.{CellLine, CellClick, CellClickResult}
import spray.json.DefaultJsonProtocol

object JsonHandler extends DefaultJsonProtocol {
  implicit val cellClick = jsonFormat2(CellClick)
  implicit val cellClickResult = jsonFormat3(CellClickResult)
  implicit val cellLine = jsonFormat2(CellLine)
}

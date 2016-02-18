package ru.agny.zcross.utils

import ru.agny.zcross.engine.messages._
import spray.json.DefaultJsonProtocol

object JsonHandler extends DefaultJsonProtocol {
  implicit val cellClick = jsonFormat3(CellClick)
  implicit val cellClickResult = jsonFormat3(CellClickResult)
  implicit val cellLine = jsonFormat2(CellLine)
  implicit val gameOver = jsonFormat2(GameOver)
  implicit val playerLeft = jsonFormat1(PlayerLeft)
  implicit val playerContinue = jsonFormat1(PlayerContinue)

  implicit val gameConfig = jsonFormat5(GameConfig)

  implicit val wsMessage = jsonFormat2(WSMessage)
}

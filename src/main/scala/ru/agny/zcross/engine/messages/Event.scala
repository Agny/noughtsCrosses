package ru.agny.zcross.engine.messages

import akka.actor.ActorRef
import ru.agny.zcross.Score

sealed trait Event
sealed trait GameEvent extends Event
sealed trait SystemEvent extends Event

case class PlayerJoined(user: String, actor: ActorRef) extends SystemEvent
case class PlayerLeft(user: String) extends SystemEvent

case class CellClick(x: Int, y: Int) extends GameEvent
case class CellClickResult(x: Int, y: Int, v: String) extends GameEvent
case class CellLine(from: CellClick, to: CellClick) extends GameEvent
case class GameOver(winner:String, score:Score) extends GameEvent
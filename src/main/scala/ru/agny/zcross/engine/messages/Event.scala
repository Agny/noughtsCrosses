package ru.agny.zcross.engine.messages

import akka.actor.ActorRef

sealed trait Event
sealed trait GameEvent extends Event
sealed trait SystemEvent extends Event

case class PlayerJoined(user: String, actor: ActorRef) extends SystemEvent
case class PlayerLeft(user: String) extends SystemEvent
case class PlayersReady(playerOne: String, playerTwo: String) extends SystemEvent
case class PlayerContinue(user:String) extends SystemEvent

case class CellClick(player: String, x: Int, y: Int) extends GameEvent
case class CellClickResult(x: Int, y: Int, v: String) extends GameEvent
case class CellLine(from: CellClick, to: CellClick) extends GameEvent
case class GameOver(winner: String, score: Map[String, Int]) extends GameEvent
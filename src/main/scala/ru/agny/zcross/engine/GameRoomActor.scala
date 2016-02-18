package ru.agny.zcross.engine

import akka.actor.{ActorRef, Actor}
import ru.agny.zcross.engine.messages._

import scala.collection.mutable

class GameRoomActor(sessionId: Int, gameContext:ActorRef) extends Actor with akka.actor.ActorLogging {

  val players = mutable.Map.empty[String, ActorRef]
  val waitingForApprove = mutable.MutableList[String]()
  val capacity = 2
  var cursor = Cursor()

  override def receive: Receive = {
    case PlayerJoined(name, actorRef) =>
      log.debug(s"Player joined: $name")
      players += name -> actorRef
      if (players.size == capacity) {
        val firstPlayers = players.take(capacity).keys
        cursor = Cursor(firstPlayers.head, firstPlayers.last)
        gameContext ! PlayersReady(firstPlayers.head, firstPlayers.last)
      }
    case msg@PlayerLeft(name) =>
      log.debug(s"Player left: $name")
      players -= name
      waitingForApprove.clear()
      broadcast(msg)
    case msg@PlayerContinue(name) =>
      log.debug(s"Up for another game: $name")
      waitingForApprove += name
      waitingForApprove.intersect(players.keys.toList).size match {
        case x if x == capacity => gameContext ! msg
        case _ => None
      }

    case msg: GameEvent => msg match {
      case msg@CellClick(player, x, y) => player match {
        case p if p == cursor.current =>
          cursor.next
          gameContext ! msg
        case _ => log.debug(s"Attempt to turn twice in a row: $player")
      }
      case msg@_ =>
        gameContext ! msg
        broadcast(msg)
    }
  }

  def broadcast(message: Event): Unit = {
    log.debug(s"Broadcasting game event $message")
    players.values.foreach(_ ! message)
  }

  case class Cursor(first:String, second:String) {
    private val turn = Seq(first, second)
    private var i = 0

    def current = {
     turn(i % 2)
    }

    def next = {
      i = i + 1
      turn(i % 2)
    }
  }

  object Cursor {
    def apply():Cursor = Cursor("","")
  }

}

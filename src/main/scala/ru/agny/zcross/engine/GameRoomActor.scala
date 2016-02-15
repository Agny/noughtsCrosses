package ru.agny.zcross.engine

import akka.actor.{ActorRef, Actor}
import ru.agny.zcross.engine.messages._

class GameRoomActor(sessionId: Int, gameContext:ActorRef) extends Actor with akka.actor.ActorLogging {

  var players: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case PlayerJoined(name, actorRef) =>
      log.debug(s"Player joined: $name")
      players += name -> actorRef
      if (players.size > 1) {
        val firstPlayers = players.take(2).keys
        gameContext ! PlayersReady(firstPlayers.head, firstPlayers.last)
      }
    case PlayerLeft(name) =>
      log.debug(s"Player left: $name")
      players -= name
    case msg: GameEvent => msg match {
      case msg@CellClick(player, x, y) => gameContext ! msg
      case msg@_ =>
        gameContext ! msg
        broadcast(msg)
    }
  }

  def broadcast(message: Event): Unit = {
    log.debug(s"Broadcasting game event $message")
    players.values.foreach(_ ! message)
  }

}

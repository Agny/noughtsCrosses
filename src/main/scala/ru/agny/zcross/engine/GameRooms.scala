package ru.agny.zcross.engine

import akka.actor.ActorSystem

object GameRooms {
  var gameRooms = Map.empty[Int, GameRoom]

  def findOrCreate(id: Int, user: String)(implicit actorSystem: ActorSystem): GameRoom = gameRooms.getOrElse(id, createNewGameRoom(id))

  private def createNewGameRoom(id: Int)(implicit actorSystem: ActorSystem): GameRoom = {
    val gameRoom = GameRoom(id)
    gameRooms += id -> gameRoom
    gameRoom
  }

}

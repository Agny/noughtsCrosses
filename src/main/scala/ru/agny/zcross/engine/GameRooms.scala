package ru.agny.zcross.engine

import akka.actor.ActorSystem

object GameRooms {
  var gameRooms = Map.empty[Int, GameRoom]

  def findOrCreate(id: Int, user: String)(implicit actorSystem: ActorSystem): GameRoom = gameRooms.getOrElse(id, createNewGameRoom(id, user))

  private def createNewGameRoom(id: Int, user: String)(implicit actorSystem: ActorSystem): GameRoom = {
    val gameRoom = GameRoom(id, user)
    gameRooms += id -> gameRoom
    gameRoom
  }

}

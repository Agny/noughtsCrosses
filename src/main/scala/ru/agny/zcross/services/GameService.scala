package ru.agny.zcross.services

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import ru.agny.zcross.engine.GameRooms

object GameService extends Directives{

  def route(implicit actorSystem: ActorSystem, materializer: Materializer): Route = path("ws-chat" / IntNumber) { roomId =>
    parameter('name) { userName =>
      handleWebsocketMessages(GameRooms.findOrCreate(roomId).websocketFlow(userName))
    }
  }
}

package ru.agny.zcross.engine

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{FlowShape, OverflowStrategy}
import akka.stream.scaladsl._
import ru.agny.zcross.GameContext

import ru.agny.zcross.engine.messages._

class GameRoom(id: Int, gameHost: String, actorSystem: ActorSystem) {
  private[this] val gameContext = actorSystem.actorOf(Props(classOf[GameContext], gameHost))
  private[this] val gameRoomActor = actorSystem.actorOf(Props(classOf[GameRoomActor], id, gameContext))
  private val source = Source.actorRef[Event](bufferSize = 15, OverflowStrategy.dropNew)

  def websocketFlow(user: String) = Flow.fromGraph(GraphDSL.create(source) { implicit b => csource =>
    import GraphDSL.Implicits._
    import ru.agny.zcross.utils.JsonHandler._
    import spray.json._

    val fromWebsocket = b.add(
      Flow[Message].collect({
        case TextMessage.Strict(txt) => txt.parseJson.convertTo[CellClick]
      })
    )
    val backToWebsocket = b.add(
      Flow[Event].map {
        case msg@CellClickResult(x, y, v) =>
          val resp = (msg.toJson.asJsObject.fields + ("type" -> JsString("cell"))).toJson.toString()
          TextMessage(resp)
        case msg@CellLine(from, to) =>
          val resp = (msg.toJson.asJsObject.fields + ("type" -> JsString("line"))).toJson.toString()
          TextMessage(resp)
        case msg@GameOver(winner, score) =>
          val resp = (msg.toJson.asJsObject.fields + ("type" -> JsString("result"))).toJson.toString()
          TextMessage(resp)
        case msg@_ =>
          println(msg)
          TextMessage(s"clicked $msg")
      }
    )

    val gameActorSink = Sink.actorRef[Event](gameRoomActor, PlayerLeft(user))
    val merge = b.add(Merge[Event](2))
    val actorAsSource = b.materializedValue.map(a => PlayerJoined(user, a))

    fromWebsocket ~> merge.in(0)
    actorAsSource ~> merge.in(1)
    merge ~> gameActorSink
    csource ~> backToWebsocket
    FlowShape(fromWebsocket.in, backToWebsocket.out)
  })
}

object GameRoom {
  def apply(id: Int, user: String)(implicit actorSystem: ActorSystem) = new GameRoom(id, user, actorSystem)
}

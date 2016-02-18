package ru.agny.zcross

import akka.actor.Actor
import ru.agny.zcross.engine.messages._

import scala.collection.mutable

class GameContext extends Actor with akka.actor.ActorLogging {
  private val crosses = mutable.MutableList[Cross]()
  private val zeros = mutable.MutableList[Zero]()
  private val duocr = mutable.MutableList[Line[DisplayCell]]()
  private val duozr = mutable.MutableList[Line[DisplayCell]]()

  private var score = Score()

  override def receive: Receive = {
    case PlayersReady(playerOne, playerTwo) =>
      log.debug(s"Players are ready: $playerOne vs $playerTwo")
      score = Score(Map(playerOne -> 0, playerTwo -> 0))
      context.become(gameStarted)
    case PlayerContinue(player) =>
      log.debug(s"Next game. $score")
      context.become(gameStarted)
  }

  def gameStarted: Receive = {
    case CellClick(player, x, y) =>
      log.debug(s"$player clicked at [$x:$y]")
      val cell = turn(player, x, y)
      sender() ! CellClickResult(cell.x, cell.y, cell.v)
  }

  private def turn(player: String, x: Int, y: Int): DisplayCell = {
    val cell = Cursor.next(x, y)
    cell match {
      case c: Cross =>
        crosses.foreach(cr => if (cr.relatesTo(c)) {
          duocr += Line(cr, c)
        })
        crosses += c

        for (d1 <- duocr; d2 <- duocr) {
          d1.checkRelation(d2)
        }
      case z: Zero =>
        zeros.foreach(zr => if (zr.relatesTo(z)) {
          duozr += Line(zr, z)
        })
        zeros += z

        for (d1 <- duozr; d2 <- duozr) {
          d1.checkRelation(d2)
        }
    }
    val res = (duocr ++ duozr).collectFirst(handleLines(player, getWinLine).andThen(prepareContextChange)).flatten
    if (res.nonEmpty) {
      clear()
      context.become(res.get)
    }
    cell
  }

  private def handleLines(player: String, f: String => Line[DisplayCell] => Option[CellLine]): PartialFunction[Line[DisplayCell], Option[(CellLine, GameOver)]] = {
    case line: Line[DisplayCell] if f(player)(line).nonEmpty =>
      f(player)(line).map {
        case mbLine: CellLine =>
          score = score.win(player)
          (mbLine, GameOver(player, score.data))
      }
  }

  private def getWinLine(player: String)(line: Line[DisplayCell]): Option[CellLine] = {
    line.winLine match {
      case list@(head :: tail) if head != list.last =>
        Some(CellLine(CellClick(player, head.x, head.y), CellClick(player, tail.last.x, tail.last.y)))
      case _ => None
    }
  }

  private def prepareContextChange(messages: Option[(CellLine, GameOver)]): Option[Receive] = {
    messages.flatMap {
      case (l, g) =>
        sender() ! l
        sender() ! g
        Some(receive)
      case _ => None
    }
  }

  private def clear(): Unit = {
    crosses.clear()
    zeros.clear()
    duocr.clear()
    duozr.clear()
  }

  object Cursor {
    private val turn = Seq(Zero, Cross)
    private var i = 0

    def next(x: Int, y: Int) = {
      i = i + 1
      turn(i % 2)(x, y)
    }
  }
}
package ru.agny.zcross

import akka.actor.Actor
import ru.agny.zcross.engine.messages._

import scala.collection.mutable

class GameContext(gameHost: String) extends Actor with akka.actor.ActorLogging {
  private val crosses = mutable.MutableList[Cross]()
  private val zeros = mutable.MutableList[Zero]()
  private val duocr = mutable.MutableList[Line[Cross]]()
  private val duozr = mutable.MutableList[Line[Zero]]()

  private var score = ScoreCompanion()

  override def receive: Receive = {
    case PlayersReady(playerOne, playerTwo) =>
      log.debug(s"Players are ready: $playerOne vs $playerTwo")
      score = Score(playerOne, playerTwo)
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
    var isGameOver = false
    for (line <- duocr) {
      if (line.isLongEnough) {
        isGameOver = true
        val head = line.values.head
        val last = line.values.last
        sender() ! CellLine(CellClick(player, head.x, head.y), CellClick(player, last.x, last.y))
      }
    }
    for (line <- duozr) {
      if (line.isLongEnough) {
        isGameOver = true
        val head = line.values.head
        val last = line.values.last
        sender() ! CellLine(CellClick(player, head.x, head.y), CellClick(player, last.x, last.y))
      }
    }
    if (isGameOver) {
      score.win()
      sender() ! GameOver(player, score.getData)
      context.become(receive)
    }
    cell
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
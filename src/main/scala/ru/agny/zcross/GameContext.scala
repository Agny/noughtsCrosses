package ru.agny.zcross

import akka.actor.Actor
import ru.agny.zcross.engine.messages.{CellLine, CellClick, CellClickResult}

class GameContext extends Actor {
  private var crosses = Seq[Cross]()
  private var zeros = Seq[Zero]()
  private var duocr = Seq[Line[Cross]]()
  private var duozr = Seq[Line[Zero]]()

  override def receive: Receive = {
    case CellClick(x, y) =>
      val cell = turn(x, y)
      sender() ! CellClickResult(cell.x, cell.y, cell.v)
  }

  private def turn(x: Int, y: Int): DisplayCell = {
    val cell = Cursor.next(x, y)
    cell match {
      case c: Cross =>
        crosses.foreach(cr => if (cr.relatesTo(c)) {
          duocr = duocr :+ Line(cr, c)
        })
        crosses = crosses :+ c

        for (d1 <- duocr; d2 <- duocr) {
          d1.checkRelation(d2)
        }
      case z: Zero =>
        zeros.foreach(zr => if (zr.relatesTo(z)) {
          duozr = duozr :+ Line(zr, z)
        })
        zeros = zeros :+ z

        for (d1 <- duozr; d2 <- duozr) {
          d1.checkRelation(d2)
        }
    }
    for (line <- duocr) {
      if (line.isLongEnough) {
        val head = line.values.head
        val last = line.values.last
        sender() ! CellLine(CellClick(head.x, head.y), CellClick(last.x, last.y))
      }
    }
    for (line <- duozr) {
      if (line.isLongEnough) {
        val head = line.values.head
        val last = line.values.last
        sender() ! CellLine(CellClick(head.x, head.y), CellClick(last.x, last.y))
      }
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
package ru.agny.zcross

import scala.scalajs.js
import scala.scalajs.js.JSApp
import org.scalajs.jquery.jQuery
import com.scalawarrior.scalajs.createjs._
import org.scalajs.dom.ext._

object Index extends JSApp {

  def main(): Unit = {
    jQuery(setupUI _)
  }

  def setupUI(): Unit = {
    val cells = Graphics.generateCells()
    cells.map(c => c.addEventListener("click", (e: Object) => {
      c.removeAllEventListeners("click")
      clickCell(e)
    }))
    Graphics.addToStage(cells)
  }

  private def clickCell(e: Object): Boolean = {
    val event = e.cast[MouseEvent]
    makeTurn(event)
    true
  }

  private def makeTurn(event: MouseEvent) = {
    Graphics.drawText(event)
  }

}

class Context {
  private var crosses = Seq[Cross]()
  private var zeros = Seq[Zero]()
  private var duocr = Seq[Duo[Cross]]()
  private var duozr = Seq[Duo[Zero]]()

  def turn(x: Int, y: Int): DisplayCell = {
    val cell = Cursor.next(x, y)
    cell match {
      case c: Cross =>
        crosses.foreach(cr => if (cr.relatesTo(c)) {
          duocr = duocr :+ Duo(cr, c)
        })
        crosses = crosses :+ c

        for(d1 <- duocr; d2 <- duocr) {
          if (d1.relatesTo(d2)) {
            Graphics.drawLine(d1.c1, d2.c2)
            println(d1 +" + " + d2)
            println("Lyuto win")
          }
        }
      case z: Zero =>
        zeros.foreach(zr => if (zr.relatesTo(z)) {
          duozr = duozr :+ Duo(zr, z)
        })
        zeros = zeros :+ z

        for(d1 <- duozr; d2 <- duozr) {
          if (d1.relatesTo(d2)) {
            Graphics.drawLine(d1.c1, d2.c2)
            println(d1 +" + " + d2)
            println("Lyuto win")
          }
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

object Context {
  private val context = new Context()

  def turn(x: Int, y: Int) = context.turn(x, y)
}

trait TextOptions extends js.Object {
  val x: Double = js.native
  val y: Double = js.native
  val scaleX: Double = js.native
  val scaleY: Double = js.native
}

object TextOptions {
  def apply(x: Double, y: Double, scaleX: Double, scaleY: Double): TextOptions = {
    js.Dynamic.literal(x = x, y = y, scaleX = scaleX, scaleY = scaleY).asInstanceOf[TextOptions]
  }
}

package ru.agny.zcross

import scala.scalajs.js
import scala.scalajs.js.JSApp
import org.scalajs.jquery.jQuery
import com.scalawarrior.scalajs.createjs._
import org.scalajs.dom.ext._

object Index extends JSApp {
  val maxW, maxH = 400
  val size = 3
  val side = maxW / size
  val pixelBorder = 0.5
  lazy val stage = new Stage("canvas")
  lazy val g = new Graphics

  def main(): Unit = {
    jQuery(setupUI _)
  }

  def setupUI(): Unit = {
    val cells = generateCells()
    cells.map(c => c.addEventListener("click", (e: Object) => clickCell(e)))
    stage.addChild(cells: _*)
    stage.update()
  }

  private def generateCells(): Seq[DisplayObject] = {
    g.setStrokeStyle(1)
    g.beginStroke("black")
    for (
      x <- 1 until maxW by maxW / size;
      y <- 1 until maxH by maxH / size
    ) yield {
      val cell = getContainer(x,y)
      val shape = new Shape(g.beginFill("white").drawRect(0, 0, side, side))
      cell.addChild(shape)
      cell
    }
  }

  private def clickCell(e: Object): Boolean = {
    val event = e.cast[MouseEvent]
    makeTurn(event)
    true
  }
  
  private def makeTurn(event:MouseEvent) = {
    val cont = event.currentTarget.cast[Container]
    val pos = cont.name
    if (Context.checkPos(pos)) {
      val text = new Text(Context.get(pos).v, "", "black")
      text.x = event.localX
      text.y = event.localY
      cont.addChild(text)
      draw()

      def draw(): Unit = {
        val scale = 10
        val tweenCfg = TextOptions((side - text.getMeasuredWidth() * scale) / 2, (side - text.getMeasuredHeight() * (scale + 1)) / 2, scale, scale)
        new Tween(text).to(tweenCfg, 200).call((tw: Tween) => {})
        Ticker.setFPS(20)
        Ticker.addEventListener("tick", stage)
      }
    }
  }

  private def getContainer(x:Double, y:Double) = {
    val cell = new Container
    cell.name = s"${(x / side).toInt}-${(y / side).toInt}"
    cell.x = x + pixelBorder
    cell.y = y + pixelBorder
    cell
  }
}

class Context {
  private var currentTurn:Turn = TurnZero()
  private val board = Array.ofDim[Turn](3,3)
  private val formatSplit = "-"
  def handleAndGet(pos:String):Turn = {
    checkMap(pos)
    currentTurn
  }

  private def checkMap(pos:String):Boolean = {
    val p = pos.split(formatSplit)
    val x = p(0).toInt
    val y = p(1).toInt
    board(x)(y) match {
      case t:Turn =>
        false
      case _ =>
        currentTurn match {
          case x:TurnCross => currentTurn = TurnZero()
          case z:TurnZero => currentTurn = TurnCross()
        }
        board(x)(y) = currentTurn
        true
    }
  }
}

object Context {
  private val context = new Context()
  def get(pos:String) = context.handleAndGet(pos)
  def checkPos(pos:String) = context.checkMap(pos)
}

// think about value classes
class Turn(val v:String) {
}
case class TurnCross() extends Turn("X")
case class TurnZero() extends Turn("O")

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

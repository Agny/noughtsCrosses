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
  val formatSplit = "-"
  lazy val stage = new Stage("canvas")
  lazy val g = new Graphics

  def main(): Unit = {
    jQuery(setupUI _)
  }

  def setupUI(): Unit = {
    val cells = generateCells()
    cells.map(c => c.addEventListener("click", (e: Object) => {
      c.removeAllEventListeners("click")
      clickCell(e)
    }))
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
      val cell = getContainer(x, y)
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

  private def makeTurn(event: MouseEvent) = {
    val cont = event.currentTarget.cast[Container]
    val Array(x, y) = cont.name.split(formatSplit)
    val text = new Text(Context.turn(x.toInt, y.toInt).v, "", "black")
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

  private def getContainer(x: Double, y: Double) = {
    val cell = new Container
    cell.name = s"${(x / side).toInt}${formatSplit}${(y / side).toInt}"
    cell.x = x + pixelBorder
    cell.y = y + pixelBorder
    cell
  }
}

class Context {
  private var crosses = Seq[Cross]()
  private var zeros = Seq[Zero]()
  private var duos = Seq[Duo[Cell]]()

  def turn(x: Int, y: Int): DisplayCell = {
    Cursor.next(x, y)
    //some logic processing
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

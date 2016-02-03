package ru.agny.zcross.ui.js

import com.scalawarrior.scalajs.createjs.{Graphics => CreateJS, _}
import org.scalajs.dom.ext._
import ru.agny.zcross._
import ru.agny.zcross.engine.AI
import ru.agny.zcross.ui.GraphicsT
import ru.agny.zcross.utils.{GraphicsUtil, PropertiesHolder}

import scala.collection.mutable

class Graphics extends GraphicsT {

  private lazy val g = new CreateJS
  private lazy val main = new Stage("main")
  private lazy val background = new Stage("background")
  private lazy val context = new Context(this)
  private val pixelBorder = 0.5
  private val pixelShift = 20
  private val formatSplit = "-"
  private val borderSide = PropertiesHolder.borderSide
  private val boardSize = PropertiesHolder.boardSize
  private val cellSide = borderSide / boardSize
  private val backShapeName = "backShape"
  private val AI = new AI(context)
  private var cells: mutable.Map[(Int, Int), Container] = null

  private def init(): Unit = {
    val boardShape = new Shape(g.drawRect(0, 0, 500, 500))
    boardShape.name = backShapeName
    background.addChild(boardShape)
    background.addEventListener("click", (e: Object) => {
      background.removeAllEventListeners("click")
      background.dispatchEvent(e)
    })

    Ticker.setFPS(20)
    Ticker.addEventListener("tick", main)
  }

  init()

  def setupUI(): Unit = {
    val cells = generateCells()
    cells.map(c => c.addEventListener("click", (e: Object) => {
      c.removeAllEventListeners("click")
      clickCell(e)
    }))
    addToStage(cells)
  }

  override def drawTurn(cell: DisplayCell): Unit = {
    val text = new Text(cell.v, "", "black")
    text.x = cell.x * cellSide + cellSide / 2
    text.y = cell.y * cellSide + cellSide / 2
    val container = cells(cell.x, cell.y)
    container.addChild(text)
    container.removeAllEventListeners("click")
    draw()

    def draw(): Unit = {
      val scale = 10
      val tweenCfg = TextOptions((cellSide - text.getMeasuredWidth() * scale) / 2, (cellSide - text.getMeasuredHeight() * (scale + 1)) / 2, scale, scale)
      new Tween(text).to(tweenCfg, 200).call((tw: Tween) => {})
    }

  }

  override def drawLine(from: CellView, to: CellView) = {
    val backShape = background.getChildByName(backShapeName).cast[Shape].graphics
    backShape.setStrokeStyle(2)
    backShape.beginStroke("red")
    val shiftX = GraphicsUtil.getCellShift(from.x, to.x)
    val shiftY = GraphicsUtil.getCellShift(from.y, to.y)
    val startBoundaryX = GraphicsUtil.getBoundaryShift(shiftX._1, pixelShift)
    val endBoundaryX = GraphicsUtil.getBoundaryShift(shiftX._2, pixelShift)
    val startBoundaryY = GraphicsUtil.getBoundaryShift(shiftY._1, pixelShift)
    val endBoundaryY = GraphicsUtil.getBoundaryShift(shiftY._2, pixelShift)
    if (from.y == to.y) {
      backShape.moveTo((from.x + shiftX._1) * cellSide + startBoundaryX, from.y * cellSide + cellSide / 2)
      backShape.lineTo((to.x + shiftX._2) * cellSide + endBoundaryX, to.y * cellSide + cellSide / 2)
    } else if (from.x == to.x) {
      backShape.moveTo(from.x * cellSide + cellSide / 2, (from.y + shiftY._1) * cellSide + startBoundaryY)
      backShape.lineTo(to.x * cellSide + cellSide / 2, (to.y + shiftY._2) * cellSide + endBoundaryY)
    } else {
      backShape.moveTo((from.x + shiftX._1) * cellSide + startBoundaryX, (from.y + shiftY._1) * cellSide + startBoundaryY)
      backShape.lineTo((to.x + shiftX._2) * cellSide + endBoundaryX, (to.y + shiftY._2) * cellSide + endBoundaryY)
    }
    backShape.endStroke()
    background.update()
  }

  override def reload(): Unit = {

  }

  private def clickCell(e: Object): Boolean = {
    val event = e.cast[MouseEvent]
    drawText(event)
    true
  }

  private def drawText(event: MouseEvent) = {
    val cont = event.currentTarget.cast[Container]
    val Array(x, y) = cont.name.split(formatSplit)
    val turn = context.turn(x.toInt, y.toInt)
    val text = new Text(turn.v, "", "black")
    text.x = event.localX
    text.y = event.localY
    cont.addChild(text)
    draw()

    def draw(): Unit = {
      val scale = 10
      val tweenCfg = TextOptions((cellSide - text.getMeasuredWidth() * scale) / 2, (cellSide - text.getMeasuredHeight() * (scale + 1)) / 2, scale, scale)
      new Tween(text).to(tweenCfg, 200).call((tw: Tween) => {})
    }
    AI.makeTurn(turn)
  }

  private def generateCells(): Seq[DisplayObject] = {
    cells = mutable.Map.empty[(Int, Int), Container]
    val generate = {
      for (
        x <- 1 until borderSide by cellSide;
        y <- 1 until borderSide by cellSide
      ) yield {
        val cell = createContainer(x, y)
        val shape = new Shape()
        shape.graphics.setStrokeStyle(1)
        shape.graphics.beginStroke("black")
        shape.graphics.beginFill("white")
        shape.graphics.drawRect(0, 0, cellSide, cellSide)
        shape.graphics.endStroke()
        cell.addChild(shape)
        cells += (x / cellSide, y / cellSide) -> cell
        cell
      }
    }
    generate
    //    drawCells(generate)
  }

  //  private def drawCells(generator: => Seq[DisplayObject]): Seq[DisplayObject] = {
  //    g.setStrokeStyle(1)
  //    g.beginStroke("black")
  //    val res = generator
  //    g.endStroke()
  //    res
  //  }

  private def createContainer(x: Double, y: Double) = {
    val cell = new Container
    cell.name = s"${(x / cellSide).toInt}$formatSplit${(y / cellSide).toInt}"
    cell.x = x + pixelBorder
    cell.y = y + pixelBorder
    cell
  }

  private def addToStage(obj: Seq[DisplayObject]) = {
    main.addChild(obj: _*)
  }
}

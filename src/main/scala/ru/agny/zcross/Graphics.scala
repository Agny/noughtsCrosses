package ru.agny.zcross

import com.scalawarrior.scalajs.createjs.{Graphics => CreateJS, _}
import org.scalajs.dom.ext._

class Graphics {

  private lazy val g = new CreateJS
  private lazy val stage = new Stage("canvas")
  private val pixelBorder = 0.5
  private val formatSplit = "-"
  private val borderSide = 400
  private val boardSize = 3
  private val cellSide = borderSide / boardSize
  private val boardName = "main"
  private val boardShapeName = "mainShape"

  private def init(): Unit = {
    val board = new Container()
    board.name = boardName
    val boardShape = new Shape(g.drawRect(0,0, 500, 500))
    boardShape.name = boardShapeName
    board.addChild(boardShape)
    stage.addChild(board)

    Ticker.setFPS(20)
    Ticker.addEventListener("tick", stage)
  }
  init()

  private def drawLine(from: Cell, to: Cell) = {
    val mainShape = stage.getChildByName(boardName).cast[Container].getChildByName(boardShapeName).cast[Shape].graphics //draw over other shapes??
    mainShape.setStrokeStyle(1)
    mainShape.beginStroke("red")
    mainShape.moveTo(from.x * cellSide, from.y * cellSide)
    mainShape.lineTo(to.x * cellSide, to.y * cellSide)
    mainShape.endStroke()
  }

  private def drawText(event: MouseEvent) = {
    val cont = event.currentTarget.cast[Container]
    val Array(x, y) = cont.name.split(formatSplit)
    val text = new Text(Context.turn(x.toInt, y.toInt).v, "", "black")
    text.x = event.localX
    text.y = event.localY
    cont.addChild(text)
    draw()

    def draw(): Unit = {
      val scale = 10
      val tweenCfg = TextOptions((cellSide - text.getMeasuredWidth() * scale) / 2, (cellSide - text.getMeasuredHeight() * (scale + 1)) / 2, scale, scale)
      new Tween(text).to(tweenCfg, 200).call((tw: Tween) => {})
    }
  }

  private def generateCells(): Seq[DisplayObject] = {
    val generate = {
      for (
        x <- 1 until borderSide by cellSide;
        y <- 1 until borderSide by cellSide
      ) yield {
        val cell = getContainer(x, y)
        val shape = new Shape()
        shape.graphics.setStrokeStyle(1)
        shape.graphics.beginStroke("black")
        shape.graphics.beginFill("white")
        shape.graphics.drawRect(0, 0, cellSide, cellSide)
        shape.graphics.endStroke()
        cell.addChild(shape)
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

  private def getContainer(x: Double, y: Double) = {
    val cell = new Container
    cell.name = s"${(x / cellSide).toInt}$formatSplit${(y / cellSide).toInt}"
    cell.x = x + pixelBorder
    cell.y = y + pixelBorder
    cell
  }

  private def addToStage(obj: Seq[DisplayObject]) = {
    val board = stage.getChildByName(boardName).cast[Container]
    board.addChild(obj: _*)
  }

}

object Graphics {
  private val g = new Graphics

  def drawLine(from: Cell, to: Cell) = g.drawLine(from, to)

  def generateCells(): Seq[DisplayObject] = g.generateCells()

  def drawText(event: MouseEvent) = g.drawText(event)

  def addToStage(obj: Seq[DisplayObject]) = g.addToStage(obj)

}

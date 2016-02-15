package ru.agny.zcross.ui.desktop

import ru.agny.zcross.engine.AI
import ru.agny.zcross.ui.GraphicsT
import ru.agny.zcross.utils.{GraphicsUtil, PropertiesHolder}
import ru.agny.zcross.{CellView, GameContext, DisplayCell}

import scala.collection.mutable
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color

object Graphics extends JFXApp with GraphicsT {

  private val borderSide = PropertiesHolder.borderSide
  private val boardSize = PropertiesHolder.boardSize
  private val cellSide = borderSide / boardSize

  private var context:GameContext = null
  private var AI = new AI(context)
  private var coordsToCell: mutable.Map[(Int, Int), CellView] = null
  private var canvas: Canvas = null
  private var gc: GraphicsContext = null

  private def setup() = {
    context = new GameContext
    AI = new AI(context)
    coordsToCell = mutable.Map.empty[(Int, Int), CellView]
    canvas = new Canvas(borderSide + 100, borderSide + 100)
    gc = canvas.graphicsContext2D
    stage = new PrimaryStage {
      title = "Canvas"
      scene = new Scene(borderSide + 300, borderSide + 300) {
        content = new VBox {
          layoutX = 30
          layoutY = 20
          spacing = 10
          children = List(
            canvas,
            new Button {
              text = "Reload"
              onAction = handle {
                reload()
              }
            }
          )
        }
        canvas.onMouseClicked = clickCell(_: MouseEvent)
      }
    }
    drawShapes(gc)
  }

  setup()

  override def drawTurn(cell: DisplayCell): Unit = {
    val c = coordsToCell.find(x => x._2.x == cell.x & x._2.y == cell.y).get
    if (!c._2.isSet) {
      c._2.isSet = true
      gc.strokeText(cell.v, c._1._1, c._1._2)
    }
  }

  override def drawLine(from: CellView, to: CellView): Unit = {
    val centerFromX = from.x * cellSide + cellSide / 2
    val centerFromY = from.y * cellSide + cellSide / 2
    val centerToX = to.x * cellSide + cellSide / 2
    val centerToY = to.y * cellSide + cellSide / 2
    val shiftX = GraphicsUtil.getCellShift(from.x, to.x)
    val shiftY = GraphicsUtil.getCellShift(from.y, to.y)

    gc.setStroke(Color.Red)
    if (from.y == to.y) {
      gc.strokeLine((from.x + shiftX._1) * cellSide, centerFromY, (to.x + shiftX._2) * cellSide, centerToY)
    } else if (from.x == to.x) {
      gc.strokeLine(centerFromX, (from.y + shiftY._1) * cellSide, centerToX, (to.y + shiftY._2) * cellSide)
    } else {
      gc.strokeLine((from.x + shiftX._1) * cellSide, (from.y + shiftY._1) * cellSide, (to.x + shiftX._2) * cellSide, (to.y + shiftY._2) * cellSide)
    }
  }

  override def reload(): Unit = {
    setup()
    AI.clear()
  }

  private def drawShapes(gc: GraphicsContext) = {
    gc.setFill(Color.Green)
    gc.setStroke(Color.Blue)
    gc.setLineWidth(1)
    generateCells(gc)
  }

  private def generateCells(gc: GraphicsContext) = {
    val generate = {
      for (
        x <- 1 until borderSide by cellSide;
        y <- 1 until borderSide by cellSide
      ) yield {
        gc.strokeRect(x, y, cellSide, cellSide)
        coordsToCell += ((x + cellSide / 2, y + cellSide / 2) -> new CellView(x / cellSide, y / cellSide))
      }
    }
    generate
  }

  private def clickCell(e: MouseEvent) = {
    val x = e.getX
    val y = e.getY
    drawText(x, y)
  }

  private def drawText(mouseX: Double, mouseY: Double) = {
    val cell = getCell(mouseX, mouseY)
    if (!cell.isSet) {
//      val code = context.turn(cell.x, cell.y)
//      gc.strokeText(code.v, mouseX, mouseY)
      cell.isSet = true
//      AI.makeTurn(cell)
    }
  }

  private def getCell(x: Double, y: Double): CellView = {
    coordsToCell.find(c => ifWithinCell(c._1._1, x) && ifWithinCell(c._1._2, y)).get._2
  }

  private def ifWithinCell(cellCenter: Double, click: Double): Boolean = {
    val maxDistance = cellSide / 2
    Math.abs(cellCenter - click) <= maxDistance
  }
}

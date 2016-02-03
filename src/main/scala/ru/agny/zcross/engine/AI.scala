package ru.agny.zcross.engine

import ru.agny.zcross.utils.PropertiesHolder
import ru.agny.zcross.{CellView, Context}

import scala.util.Random

class AI(ctx: Context) {

  private val boardSize = PropertiesHolder.boardSize
  private var boardState = generateBoard()

  def makeTurn(playerTurn: CellView) = {
    boardState(playerTurn.x, playerTurn.y).isSet = true
    ctx.g.drawTurn(randomTurn())
  }

  private def randomTurn() = {
    val cell = randomize(available().toSeq, 1)
    cell match {
      case Some(c) =>
        boardState(c.x,c.y).isSet = true
        ctx.turn(c.x, c.y)
      case None => throw new NoSuchElementException
    }
  }

  private def available() = {
    boardState.filter(x => !x._2.isSet).values
  }

  def randomize(cells: Seq[CellState], counter: Int): Option[CellState] = {
    val rnd = new Random()
    cells.find(c => c.x == rnd.nextInt(boardSize) & c.y == rnd.nextInt(boardSize)) match {
      case Some(x) => Option(x)
      case _ => if (counter > 10) {
        None
      } else {
        randomize(cells, counter + 1)
      }
    }
  }

  def clear() = {
    boardState = generateBoard()
  }

  private def generateBoard() = {
    for (x <- 0 to boardSize; y <- 0 to boardSize) yield (x, y) -> CellState(x, y)
  }.toMap
}

case class CellState(override val x: Int, override val y: Int) extends CellView(x, y) {
  var weight = 100
}


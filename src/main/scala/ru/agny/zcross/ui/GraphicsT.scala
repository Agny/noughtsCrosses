package ru.agny.zcross.ui

import ru.agny.zcross.{DisplayCell, CellView}

trait GraphicsT {

  def drawTurn(cell: DisplayCell)

  def drawLine(from: CellView, to: CellView)

  def reload()

}

package ru.agny.zcross.utils

object GraphicsUtil {

  def getCellShift(from:Int, to:Int):(Int, Int) = {
    if (from >= to) (1, 0)
    else (0, 1)
  }

  def getBoundaryShift(shiftX: Int, pixels: Int): Double = {
    Math.pow(-1, shiftX) * pixels
  }

}

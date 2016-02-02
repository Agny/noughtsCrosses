package ru.agny.zcross

import ru.agny.zcross.ui.GraphicsT


class Context(g:GraphicsT) {
  private var crosses = Seq[Cross]()
  private var zeros = Seq[Zero]()
  private var duocr = Seq[Line[Cross]]()
  private var duozr = Seq[Line[Zero]]()

  def turn(x: Int, y: Int): DisplayCell = {
    val cell = Cursor.next(x, y)
    cell match {
      case c: Cross =>
        crosses.foreach(cr => if (cr.relatesTo(c)) {
          duocr = duocr :+ Line(cr, c)
        })
        crosses = crosses :+ c

        for(d1 <- duocr; d2 <- duocr) {
          d1.checkRelation(d2)
        }
      case z: Zero =>
        zeros.foreach(zr => if (zr.relatesTo(z)) {
          duozr = duozr :+ Line(zr, z)
        })
        zeros = zeros :+ z

        for(d1 <- duozr; d2 <- duozr) {
          d1.checkRelation(d2)
        }
    }
    for(cr <- duocr) {
      if (cr.isLongEnough) {
        g.drawLine(cr.values.head, cr.values.last)
      }
    }
    for(zr <- duozr) {
      if (zr.isLongEnough) {
        g.drawLine(zr.values.head, zr.values.last)
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
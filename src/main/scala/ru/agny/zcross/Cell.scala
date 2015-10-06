package ru.agny.zcross

class Cell(val x: Int, val y: Int) {
  lazy val adjacent = {
    val r1 = Range(x - 1, x + 1)
    val r2 = Range(y - 1, y + 1)
    (for (
      k <- r1;
      j <- r2
    ) yield new Cell(k, j)) filter (c => c.x != x & c.y != y)
  }: Seq[Cell]

  def relatesTo(other: Cell): Boolean = {
    adjacent.exists(c => c.x == other.x & c.y == other.y)
  }
}

class DisplayCell(val v: String, x: Int, y: Int) extends Cell(x, y)

case class Cross(override val x: Int, override val y: Int) extends DisplayCell("X", x, y)

case class Zero(override val x: Int, override val y: Int) extends DisplayCell("O", x, y)

case class Duo[A <: Cell](c1: A, c2: A, relation: (Int, Int) => Boolean) {
  def relatesTo(c3: A): Boolean = ???
}

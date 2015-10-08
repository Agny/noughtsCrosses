package ru.agny.zcross

class Cell(val x: Int, val y: Int) {
  private lazy val adjacent = {
    val r1 = Range(x - 1, x + 2)
    val r2 = Range(y - 1, y + 2)
    (for (
      k <- r1;
      j <- r2
    ) yield new Cell(k, j)) filter (c => c.x != x | c.y != y)
  }: Seq[Cell]

  def relatesTo(other: Cell): Boolean = {
    adjacent.exists(c => c.x == other.x & c.y == other.y)
  }

  def relatesBy(other: Cell, relation: (Int, Int) => Boolean): Boolean = {
    relation(other.x -this.x, other.y -this.y)
  }

  override def toString: String = {
    s"[$x,$y]"
  }
}

class DisplayCell(val v: String, x: Int, y: Int) extends Cell(x, y)

case class Cross(override val x: Int, override val y: Int) extends DisplayCell("X", x, y)

case class Zero(override val x: Int, override val y: Int) extends DisplayCell("O", x, y)

case class Duo[A <: Cell](c1: A, c2: A) {
  private val relations = Map(
    c1 -> ((a: Int, b: Int) => a == c1.x - c2.x & b == c1.y - c2.y),
    c2 -> ((a: Int, b: Int) => a == c2.x - c1.x & b == c2.y - c1.y)
  )

  def relatesTo(c3: A): Boolean = {
    c1.relatesBy(c3, relations(c1)) | c2.relatesBy(c3, relations(c2))
  }

  def relatesTo(other: Duo[A]): Boolean = {
    relatesTo(other.c1) | relatesTo(other.c2)
  }
  override def toString: String = {
    s"$c1->$c2"
  }
}

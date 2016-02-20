package ru.agny.zcross

import ru.agny.zcross.utils.PropertiesHolder

import scala.collection.mutable

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
    relation(other.x - this.x, other.y - this.y)
  }

  override def toString: String = {
    s"[$x,$y]"
  }
}

class DisplayCell(val v: String, x: Int, y: Int) extends Cell(x, y)

case class Cross(override val x: Int, override val y: Int) extends DisplayCell("X", x, y)

case class Zero(override val x: Int, override val y: Int) extends DisplayCell("O", x, y)

case class Line[A <: Cell](c1: A, c2: A) {
  private val left = "left"
  private val right = "right"
  private val maxLength = PropertiesHolder.gameConfig.lineSize
  private val innerLine = mutable.MutableList[A](c1, c2)
  private val relations = mutable.Map(
    left -> ((dx: Int, dy: Int) => dx == innerLine.head.x - innerLine.tail.head.x &
      dy == innerLine.head.y - innerLine.tail.head.y),
    right -> ((dx: Int, dy: Int) => dx == innerLine.tail.head.x - innerLine.head.x &
      dy == innerLine.tail.head.y - innerLine.head.y)
  )

  def checkRelation(c3: A) = {
    if (innerLine.head.relatesBy(c3, relations(left))) {
      innerLine.+=:(c3)
      true
    } else if (innerLine.last.relatesBy(c3, relations(right))) {
      innerLine += c3
      true
    } else
      false
  }

  def checkRelation(other: Line[A]): Boolean = {
    checkRelation(other.innerLine.head) | checkRelation(other.innerLine.last)
  }

  def winLine: List[A] = {
    if (innerLine.length == maxLength) values
    else List.empty
  }

  private def values = {
    innerLine.toList
  }

  override def toString: String = {
    s"$innerLine.head->$innerLine.last"
  }
}

package ru.agny.zcross

case class Score(playerOne: String, playerTwo: String) {

  private var data = Map[String, Int](playerOne -> 0, playerTwo -> 0)

  def win() = data = data.map(m => if (m._1 == playerOne) m._1 -> (m._2 + 1) else m)

  def loose() = data = data.map(m => if (m._1 == playerTwo) m._1 -> (m._2 + 1) else m)

  def getData = data
}

object ScoreCompanion {
  val placeHolder = "UserEmpty"

  def apply(): Score = Score(placeHolder, placeHolder)
}

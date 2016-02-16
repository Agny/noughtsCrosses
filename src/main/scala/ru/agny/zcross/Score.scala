package ru.agny.zcross

case class Score(data: Map[String, Int]) {
  def win(winner: String) = Score(
    data.map(m => if (m._1 == winner) m._1 -> (m._2 + 1) else m)
  )
}

object Score {
  def apply(): Score = Score(Map.empty)
}

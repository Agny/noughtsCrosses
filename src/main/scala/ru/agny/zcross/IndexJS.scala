package ru.agny.zcross

import ru.agny.zcross.ui.js.Graphics

import scala.scalajs.js
import scala.scalajs.js.JSApp
import org.scalajs.jquery.jQuery

object IndexJS extends JSApp {

  val g = new Graphics()

  def main(): Unit = {
    jQuery(g.setupUI _)
  }
}

trait TextOptions extends js.Object {
  val x: Double = js.native
  val y: Double = js.native
  val scaleX: Double = js.native
  val scaleY: Double = js.native
}

object TextOptions {
  def apply(x: Double, y: Double, scaleX: Double, scaleY: Double): TextOptions = {
    js.Dynamic.literal(x = x, y = y, scaleX = scaleX, scaleY = scaleY).asInstanceOf[TextOptions]
  }
}

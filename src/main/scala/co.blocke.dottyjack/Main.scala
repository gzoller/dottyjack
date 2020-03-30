package co.blocke.dottyjack

import co.blocke.dotty_reflection._

object Main {

  def main(args: Array[String]): Unit = {

    println(analyzeType[Array[Byte]])

    val dj = DottyJack()

    println(dj.render("foom"))
    println(dj.read[String]("\"Greg\"".asInstanceOf[json.JSON]))
  }

}

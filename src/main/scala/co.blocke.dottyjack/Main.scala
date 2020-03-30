package co.blocke.dottyjack

import co.blocke.dotty_reflection._

case class Person(name: String, age: Int)

object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()

    val js:json.JSON = """{"name":"Greg","age":53}""".asInstanceOf[json.JSON]

    val p = dj.read[Person](js)
    println(p)
    println(dj.render(p))
  }

}

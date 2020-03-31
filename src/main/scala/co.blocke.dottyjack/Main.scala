package co.blocke.dottyjack

import co.blocke.dotty_reflection._

case class Foo(a: Int)
case class Person(name: String, age: Option[Foo])

object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()

    val p = Person("Greg", None)

    val js = dj.render(p)
    println(js)
    println(dj.read[Person](js))
  }

}

package co.blocke.dottyjack

import co.blocke.dotty_reflection._

case class Foo(a: Int)
case class Person(name: String, age: (Int,Foo,String), extra: (Double,Boolean))

object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()

    val p = Person("Greg", (53, Foo(5), "wow"), (12.34,true))

    val js = dj.render(p)
    println(js)
    println(dj.read[Person](js))
  }

}

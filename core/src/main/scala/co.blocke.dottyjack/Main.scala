package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._

case class Foom(a: Array[Int])

object Main {

  def main(args: Array[String]): Unit = {

    val dj = DottyJack()

    /*
    val classtag = scala.reflect.ClassTag[Int](classOf[Int])
    println(classtag)
    val builder: scala.collection.mutable.Builder[Int,Array[Int]] = 
      Array.newBuilder[Int](classtag)//.asInstanceOf[scala.collection.mutable.Builder[Int,Array[Int]]]
    builder.addOne(1)
    builder.addOne(2)
    builder.addOne(3)
    println(builder.result)
    */

    val js = """{"a":[1,2,3]}""".asInstanceOf[json.JSON]
    val i = dj.read[Foom](js)
    println(i)
    println(dj.render(i))

    /*
    println(classOf[Int].getName)
    println(Class.forName("int"))
    */
  }


  def methods(clazz: Class[_]): String = 
    s"=== Methods: ${clazz.getName} ===\n   " + clazz.getMethods.toList.mkString("\n   ")
  def fields(clazz: Class[_]): String = 
    s"=== Fields: ${clazz.getName} ===\n   " + clazz.getFields.toList.mkString("\n   ")
  def stack(clazz: Class[_]): String = 
    s"=== Superclass: ${clazz.getName} ===\n   " + clazz.getSuperclass() + "\n" +
    s"=== Interfaces: ${clazz.getName} ===\n   " + clazz.getInterfaces.toList.mkString("\n   ")
  
}
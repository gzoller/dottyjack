package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._


/*
trait Animal[T]{
  val name: String
  val numLegs: T
}
case class Dog(name: String, numLegs: Int, likesWalks: Boolean) extends Animal[Int]

trait Thing[A, B] { val a: A; val b: B }
case class AThing[Y, X](a: X, b: Y) extends Thing[X, Y]
*/

case class Bar2[X](id: X)
case class Foo2[A](x: Bar2[A], b: Int)



object Main {

  def main(args: Array[String]): Unit = 

    val sj = DottyJack()

    println(Reflector.reflectOn[Foo2[Long]])
    // val inst = Foo2(Bar2(123L), 19)
    // val js = sj.render(inst)
    // println(js)


    /*
    Note: In ScalaJack behavior like this:
      trait Foom
      trait Thing[A, B] { val a: A; val b: B }
      case class AThing[Y, X](a: X, b: Y) extends Thing[X, Y] with Foom
      object Size extends Enumeration {
        val Small, Medium, Large = Value
      }
      import Size._

      val f: Foom = AThing("Wow",Medium)
      val js = sj.render(f)
      println(js)
      val i = sj.read[Foom](js)
      println(i)
      println(i.asInstanceOf[AThing[_,_]].b.getClass)

      The Size-type is converted to a String here.  Since Foom has no type params, AThing's X and Y params default to Any.
    */


  def constructors(clazz: Class[_]): String = 
    s"=== Constructors: ${clazz.getName} ===\n   " + clazz.getConstructors.toList.mkString("\n   ")
  def methods(clazz: Class[_]): String = 
    s"=== Methods: ${clazz.getName} ===\n   " + clazz.getMethods.toList.mkString("\n   ")
  def fields(clazz: Class[_]): String = 
    s"=== Fields: ${clazz.getName} ===\n   " + clazz.getFields.toList.mkString("\n   ")
  def stack(clazz: Class[_]): String = 
    s"=== Superclass: ${clazz.getName} ===\n   " + clazz.getSuperclass() + "\n" +
    s"=== Interfaces: ${clazz.getName} ===\n   " + clazz.getInterfaces.toList.mkString("\n   ")
  
}
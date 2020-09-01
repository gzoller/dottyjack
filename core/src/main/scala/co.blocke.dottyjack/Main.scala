package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import co.blocke.dottyjack.json.JSON

// trait Thing[A, B] { val a: A; val b: B }
// case class AThing[Y, X](a: X, b: Y) extends Thing[X, Y]
// trait Part[A] { val p: A }
// case class APart[A](p: A) extends Part[A]



object Main {

  def main(args: Array[String]): Unit = 

    // val sj = DottyJack()
    val sj = co.blocke.dottyjack.DottyJack()

    // println(RType.of[(Thing[String, Int], Thing[String, Int])])
    // println(RType.of[(Thing[String, Part[Double]], Thing[String, Part[Double]])])
    // println("---------------------")
    // println(RType.of(Class.forName("co.blocke.dottyjack.AThing")))

    // println(RType.inTermsOf[(Thing[String, Int], Thing[String, Int])](Class.forName("co.blocke.dottyjack.AThing")))

    // val t1 = (AThing("wow", 4), AThing("boom", 1))
    // val t2 = (AThing("yep", 3), AThing("yikes", 11))
    // val inst = Map(t1 -> t2)
    // println(sj.render(inst))


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
package co.blocke.dottyjack

import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._
import org.apache.commons.codec.binary.Base64

import scala.collection.mutable
import scala.jdk.CollectionConverters._


// case class IntArr( a: Array[Int] )
// case class JSeqMap2( a1: java.util.HashMap[String,Seq[Int]])

object Main {

  def main(args: Array[String]): Unit = 

    val dj = DottyJack()

    // val hm1 = new java.util.HashMap[String,Seq[Int]]()
    // hm1.put("a",List(1,2,3) )
    // hm1.put("b",List(4,5,6) )

    // val inst = JSeqMap2(hm1)
    // println(dj.render(inst))


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
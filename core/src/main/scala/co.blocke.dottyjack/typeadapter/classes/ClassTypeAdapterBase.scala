package co.blocke.dottyjack
package typeadapter
package classes

import model._
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._

import scala.collection.mutable

// For case classes and Java/Scala plain classes, but not traits
trait ClassTypeAdapterBase[T] extends TypeAdapter[T] with Classish:
  val info:               Transporter.RType
  val argsTemplate:       Array[Object]
  val fieldBitsTemplate:  mutable.BitSet
  val isSJCapture:        Boolean
  val fieldMembersByName: Map[String, ClassFieldMember[_,_]]
  val isCaseClass:        Boolean = false
  val orderedFieldNames:  List[String]

  def dbKeys: List[ClassFieldMember[_,_]] =
    fieldMembersByName.values.toList
      .filter(_.dbKeyIndex.isDefined)
      .sortBy(_.dbKeyIndex.get)
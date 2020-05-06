package co.blocke.dottyjack
package typeadapter
package classes

import model._
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info._

import scala.collection.mutable

// For case classes and Java/Scala plain classes, but not traits
trait ClassTypeAdapterBase[T] extends TypeAdapter[T] with Classish:
  val info:               RType
  val argsTemplate:       Array[Object]
  val fieldBitsTemplate:  mutable.BitSet
  val isSJCapture:        Boolean
  val fieldMembersByName: Map[String, ClassFieldMember[_]]
  val isCaseClass:        Boolean = false
  val orderedFieldNames = info.asInstanceOf[ClassInfo].fields.map( f => 
    // Re-map field names if @Change annotation is present
    f.annotations.get(CHANGE_ANNO).map(_("name")).getOrElse(f.name)
  )

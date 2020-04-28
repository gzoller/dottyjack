package co.blocke.dottyjack
package typeadapter

import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._
import model._
import scala.collection.mutable

object CaseClassTypeAdapterFactory extends TypeAdapterFactory:
  
  def matches(concrete: RType): Boolean = concrete match {
    case c: ScalaClassInfo if !c.isValueClass => true
    case _ => false
  }
  
  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val classInfo = concrete.asInstanceOf[ScalaClassInfo]
    val bits = mutable.BitSet()
    val args = new Array[Object](classInfo.fields.size)

    classInfo.fields.map( f => bits += f.index )
    val fieldMembersByName = 
      classInfo.fields.map{ f => 
        val fieldMember: ClassFieldMember[_] = {
          val fieldTypeAdapter = f.fieldType match {
            case t: TypeSymbolInfo => taCache.typeAdapterOf(PrimitiveType.Scala_Any) // Any unresolved type symbols must be considered Any
            case t => taCache.typeAdapterOf(t)
          }

          // See if there's a default value set and blip bits/args accordingly to "pre-set" these values
          if f.defaultValueAccessor.isDefined then
            args(f.index) = f.defaultValueAccessor.get()
            bits -= f.index
          else if fieldTypeAdapter.defaultValue.isDefined then
            args(f.index) = fieldTypeAdapter.defaultValue.get.asInstanceOf[Object]
            bits -= f.index

          ClassFieldMember(
            f,
            fieldTypeAdapter,
            None,  // TODO
            None,  // TODO
            f.annotations.get("co.blocke.dottyjack.Change").map(_("name"))
          )
        }
        f.name -> fieldMember}.toMap

    CaseClassTypeAdapter(
      concrete,
      fieldMembersByName,
      args,
      bits,
      classInfo.typeMembers.map( tmem => (tmem.name, tmem.asInstanceOf[TypeMemberInfo]) ).toMap
    )
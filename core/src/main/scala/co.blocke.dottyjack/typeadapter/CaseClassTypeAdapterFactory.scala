package co.blocke.dottyjack
package typeadapter

import co.blocke.dotty_reflection.infos._
import co.blocke.dotty_reflection._
import model._
import scala.collection.mutable

object CaseClassTypeAdapterFactory extends TypeAdapterFactory:
  
  def matches(concrete: ConcreteType): Boolean = concrete match {
    case c: ClassInfo => true
    case _ => false
  }
  
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    val classInfo = concrete.asInstanceOf[ClassInfo]
    val bits = mutable.BitSet()
    val args = new Array[Object](classInfo.fields.size)

    classInfo.fields.map( f => bits += f.index )
    val fieldMembersByName = 
      classInfo.fields.map{ f => 
        val fieldMember: ClassFieldMember[_] = f.fieldType match {
          case c: ConcreteType =>
            val fieldTypeAdapter = taCache.typeAdapter(c)

            // See if there's a default value set and blip bits/args accordingly to "pre-set" these values
            fieldTypeAdapter.defaultValue.map{ default =>
              args(f.index) = default.asInstanceOf[Object]
              bits -= f.index
            }

            ClassFieldMember(
              f,
              fieldTypeAdapter,
              None,  // TODO
              None,  // TODO
              None   // TODO
            )
          case c => throw new ScalaJackError(s"Concrete type expected for class ${classInfo.name} field ${f.name}.  ${c.getClass.getName} was found.")
        }
        f.name -> fieldMember}.toMap

    CaseClassTypeAdapter(
      concrete,
      fieldMembersByName,
      args,
      bits
    )
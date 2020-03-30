package co.blocke.dottyjack
package typeadapter

import co.blocke.dotty_reflection.infos._
import model._
import scala.collection.mutable

object CaseClassTypeAdapterFactory extends TypeAdapterFactory:
  
  def matches(concrete: ConcreteType): Boolean = concrete match {
    case c: ClassInfo => true 
    case _ => false
  }
  
  def makeTypeAdapter(concrete: ConcreteType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    CaseClassTypeAdapter(
      concrete,
      new Array[Object](concrete.asInstanceOf[ClassInfo].fields.size),
      {
        val bits = mutable.BitSet()
        concrete.asInstanceOf[ClassInfo].fields.map( f => bits += f.index )
        bits
      }
    )
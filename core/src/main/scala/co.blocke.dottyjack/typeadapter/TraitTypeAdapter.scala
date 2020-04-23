package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.info.TraitInfo

import scala.collection.mutable

// This should come *after* SealedTraitTypeAdapter in the Context factory list, as all sealed traits are
// also traits, and this factory would pick them all up, hiding the sealed ones.
//
object TraitTypeAdapterFactory extends TypeAdapterFactory:

  def matches(concrete: RType): Boolean = 
    concrete match {
      case _: TraitInfo => true
      case _ => false
    }

  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =
    TraitTypeAdapter(concrete, taCache.jackFlavor.defaultHint)

    /*  TODO -- Wire up all modifiers and clean up unneeded crud
  override def typeAdapterOf[T](
      classSymbol: ClassSymbol,
      next:        TypeAdapterFactory
  )(implicit taCache: TypeAdapterCache, tt: TypeTag[T]): TypeAdapter[T] =
    if (classSymbol.isTrait) {
      TraitTypeAdapter(
        classSymbol.fullName,
        taCache.jackFlavor.getHintLabelFor(tt.tpe),
        tt.tpe,
        ActiveTypeParamWeaver(tt.tpe)
      //        if (tt.tpe.typeArgs.nonEmpty) ActiveTypeParamWeaver(tt.tpe) else NoOpTypeParamWeaver()
      )
    } else
      next.typeAdapterOf[T]
}*/


case class TraitTypeAdapter[T](
    info:            RType,
    hintLabel:       String
)(implicit taCache: TypeAdapterCache) extends TypeAdapter[T] with Classish:

  inline def calcTA(c: Class[_]): CaseClassTypeAdapter[T] =
    info.isParameterized match {
      case true => 
        taCache.typeAdapterOf(Reflector.reflectOnClassInTermsOf(c, info)).asInstanceOf[CaseClassTypeAdapter[T]]
      case false => 
        taCache.typeAdapterOf(Reflector.reflectOnClass(c)).asInstanceOf[CaseClassTypeAdapter[T]]
    }

  // The battle plan here is:  Scan the keys of the object looking for type typeHintField.  Perform any (optional)
  // re-working of the hint value via hintModFn.  Look up the correct concete TypeAdapter based on the now-known type
  // and re-read the object as a case class.
  def read(parser: Parser): T =
    if (parser.peekForNull)
      null.asInstanceOf[T]
    else {
      val concreteClass = parser.scanForHint(
        hintLabel,
        DefaultHintModifier).asInstanceOf[Class[T]]
        /* TODO
        taCache.jackFlavor.hintValueModifiers
          .getOrElse(polymorphicType, DefaultHintModifier)
      )
      */
      val ccta = calcTA(concreteClass)
      ccta.read(parser).asInstanceOf[T]
    }

  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    if (t == null)
      writer.writeNull(out)
    else {
      val ccta = calcTA(t.getClass)
      val hintValue = DefaultHintModifier.unapply(t.getClass.getName) // TODO: Wire up hint value modifiers
      writer.writeObject(
        t, 
        ccta.orderedFieldNames, 
        ccta.fieldMembersByName, 
        out, 
        List(
          (
            hintLabel,
            ExtraFieldValue(
              hintValue,
              taCache.jackFlavor.stringTypeAdapter
            )
          )
        )
      )
    }
package co.blocke.dottyjack
package model

import typeadapter._
import scala.util.{ Success, Try }
import co.blocke.dotty_reflection._


object TypeAdapterCache {

  val StandardFactories: List[TypeAdapterFactory] =
    List(
      /*
      BigDecimalTypeAdapterFactory,
      BigIntTypeAdapterFactory,
      BinaryTypeAdapterFactory,
      BooleanTypeAdapterFactory,
      ByteTypeAdapterFactory,
      CharTypeAdapterFactory,
      DoubleTypeAdapterFactory,
      FloatTypeAdapterFactory,
      IntTypeAdapterFactory,
      LongTypeAdapterFactory,
      ShortTypeAdapterFactory,
      */
      StringTypeAdapterFactory
      /*
      //    TypeTypeAdapterFactory,
      TypeParameterTypeAdapterFactory,
      OptionTypeAdapterFactory,
      TryTypeAdapterFactory,
      TupleTypeAdapterFactory,
      EitherTypeAdapterFactory, // Either must precede SealedTraitTypeAdapter
      UnionTypeAdapterFactory,
      EnumerationTypeAdapterFactory,
      // WARNING: These two must precede CaseClassTypeAdapter in this list or all
      //     ValueClasses will be interpreted as case classes, and case objects
      //     will likewise be hidden (interpreted as regular classes).
      SealedTraitTypeAdapterFactory,
      ValueClassTypeAdapterFactory,
      ClassTypeAdapterFactory,
      TraitTypeAdapterFactory,
      UUIDTypeAdapterFactory,
      AnyTypeAdapterFactory,
      JavaBigDecimalTypeAdapterFactory,
      JavaBigIntegerTypeAdapterFactory,
      JavaBooleanTypeAdapterFactory,
      JavaByteTypeAdapterFactory,
      JavaCharacterTypeAdapterFactory,
      JavaDoubleTypeAdapterFactory,
      JavaFloatTypeAdapterFactory,
      JavaIntTypeAdapterFactory,
      JavaLongTypeAdapterFactory,
      JavaNumberTypeAdapterFactory,
      JavaShortTypeAdapterFactory,
      DurationTypeAdapterFactory,
      InstantTypeAdapterFactory,
      LocalDateTimeTypeAdapterFactory,
      LocalDateTypeAdapterFactory,
      LocalTimeTypeAdapterFactory,
      OffsetDateTimeTypeAdapterFactory,
      OffsetTimeTypeAdapterFactory,
      PeriodTypeAdapterFactory,
      ZonedDateTimeTypeAdapterFactory,
      PlainClassTypeAdapterFactory
      */
    )
}

case class TypeAdapterCache(
    jackFlavor: JackFlavor[_],
    factories:  List[TypeAdapterFactory]) {

  sealed trait Phase
  case object Uninitialized extends Phase
  case object Initializing extends Phase
  case class Initialized(typeAdapterAttempt: Try[TypeAdapter[_]]) extends Phase

  object TypeEntryFactory extends java.util.function.Function[TypeStructure, TypeAdapter[_]]:
    override def apply(tpe: TypeStructure): TypeAdapter[_] = factories.find(_.matches(tpe)).get.makeTypeAdapter(tpe) // (it will always find one--the last in the list is a catch-all)

  private val typeEntries =
    new java.util.concurrent.ConcurrentHashMap[TypeStructure, TypeAdapter[_]]

  def withFactory(factory: TypeAdapterFactory): TypeAdapterCache =
    copy(factories = factories :+ factory)

  def typeAdapter(tpe: TypeStructure): TypeAdapter[_] =
    typeEntries.computeIfAbsent(tpe, TypeEntryFactory)

  inline def typeAdapterOf[T]: TypeAdapter[T] =
    typeAdapter(analyzeType[T]).asInstanceOf[TypeAdapter[T]]
}

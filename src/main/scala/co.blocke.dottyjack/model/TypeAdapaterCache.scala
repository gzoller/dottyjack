package co.blocke.dottyjack
package model

import typeadapter._
import scala.util.{ Success, Try }
import co.blocke.dotty_reflection._
import co.blocke.dotty_reflection.infos._


object TypeAdapterCache {

  val StandardFactories: List[TypeAdapterFactory] =
    List(
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
      StringTypeAdapterFactory,
      // TypeParameterTypeAdapterFactory,
      // OptionTypeAdapterFactory,
      // TryTypeAdapterFactory,
      TupleTypeAdapterFactory,
      // EitherTypeAdapterFactory, // Either must precede SealedTraitTypeAdapter
      // UnionTypeAdapterFactory,
      // EnumerationTypeAdapterFactory,
      // WARNING: These two must precede CaseClassTypeAdapter in this list or all
      //     ValueClasses will be interpreted as case classes, and case objects
      //     will likewise be hidden (interpreted as regular classes).
      // SealedTraitTypeAdapterFactory,
      // ValueClassTypeAdapterFactory,
      CaseClassTypeAdapterFactory
      /*
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
    factories:  List[TypeAdapterFactory]):

  sealed trait Phase
  case object Uninitialized extends Phase
  case object Initializing extends Phase
  case class Initialized(typeAdapterAttempt: Try[TypeAdapter[_]]) extends Phase


  class TypeEntry(tpe: ConcreteType):
    @volatile
    private var phase: Phase = Uninitialized

    def typeAdapter: TypeAdapter[_] = 
      val attempt =
        phase match {
          case Initialized(a) => a

          case Uninitialized | Initializing =>
            synchronized {
              phase match {
                case Uninitialized =>
                  phase = Initializing
                  val typeAdapterAttempt = Try {
                    val taCache: TypeAdapterCache = TypeAdapterCache.this
                    val foundFactory = factories.find(_.matches(tpe)).get
                    foundFactory.makeTypeAdapter(tpe)(taCache)
                  }
                  phase = Initialized(typeAdapterAttempt)
                  typeAdapterAttempt

                case Initializing =>
                  Success(LazyTypeAdapter(TypeAdapterCache.this, tpe))

                case Initialized(a) =>
                  a
              }
            }
        }
      attempt.get


  private val typeEntries = new java.util.concurrent.ConcurrentHashMap[ConcreteType, TypeEntry]

  def withFactory(factory: TypeAdapterFactory): TypeAdapterCache =
    copy(factories = factories :+ factory)

  def typeAdapter(tpe: TypeStructure): TypeAdapter[_] = 
    typeAdapter(Reflector.reflectOnType(tpe))

  def typeAdapter(concreteType: ConcreteType): TypeAdapter[_] =
    typeEntries.computeIfAbsent(concreteType, ConcreteTypeEntryFactory).typeAdapter

  inline def typeAdapterOf[T]: TypeAdapter[T] =
    typeAdapter(analyzeType[T]).asInstanceOf[TypeAdapter[T]]

  val self = this 

  object ConcreteTypeEntryFactory extends java.util.function.Function[ConcreteType, TypeEntry]:
    override def apply(concrete: ConcreteType): TypeEntry = 
      new TypeEntry(concrete)

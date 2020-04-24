package co.blocke.dottyjack
package model

import typeadapter._
import scala.collection.mutable
import co.blocke.dotty_reflection._

trait JackFlavor[WIRE]: // extends Filterable[WIRE] with ViewSplice {

  type WIRE_TYPE = WIRE

  def parse(input: WIRE): Parser

  val defaultHint: String        = "_hint"
  val stringifyMapKeys: Boolean  = false
  // TODO 
  // val hintMap: Map[Type, String] = Map.empty[Type, String]
  // val hintValueModifiers: Map[Type, HintValueModifier] =
  //   Map.empty[Type, HintValueModifier]
  val typeValueModifier: HintValueModifier     = DefaultHintModifier
  val enumsAsInt: Boolean                      = false
  // val customAdapters: List[TypeAdapterFactory] = List.empty[TypeAdapterFactory]
  val parseOrElseMap: Map[RType, RType]        = Map.empty[RType, RType]
  val permissivesOk: Boolean                   = false

  lazy val taCache: TypeAdapterCache = bakeCache()

  def bakeCache(): TypeAdapterCache = 
    val permissives =
      if (permissivesOk)
        List(
          PermissiveBigDecimalTypeAdapterFactory,
          PermissiveBigIntTypeAdapterFactory,
          PermissiveBooleanTypeAdapterFactory,
          PermissiveByteTypeAdapterFactory,
          PermissiveDoubleTypeAdapterFactory,
          PermissiveFloatTypeAdapterFactory,
          PermissiveIntTypeAdapterFactory,
          PermissiveLongTypeAdapterFactory,
          PermissiveShortTypeAdapterFactory,
          PermissiveJavaBigDecimalTypeAdapterFactory,
          PermissiveJavaBigIntegerTypeAdapterFactory,
          PermissiveJavaBooleanTypeAdapterFactory,
          PermissiveJavaByteTypeAdapterFactory,
          PermissiveJavaDoubleTypeAdapterFactory,
          PermissiveJavaFloatTypeAdapterFactory,
          PermissiveJavaIntTypeAdapterFactory,
          PermissiveJavaLongTypeAdapterFactory,
          PermissiveJavaNumberTypeAdapterFactory,
          PermissiveJavaShortTypeAdapterFactory
        )
      else
        List.empty[TypeAdapterFactory]

    // TODO
    // val intermediateContext = TypeAdapterCache(
    //   this,
    //   List(
    //     typeadapter.CollectionTypeAdapterFactory(this, enumsAsInt),
    //     customAdapters ::: TypeAdapterCache.StandardFactories
    //   )
    // )

    // ParseOrElse functionality
    val parseOrElseFactories: List[TypeAdapterFactory] = parseOrElseMap.map {
      case (attemptedType, fallbackType) => //@ _) =>
        new TypeAdapterFactory {
          def matches(concrete: RType): Boolean = concrete == attemptedType
        
          def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] = 
            FallbackTypeAdapter( taCache.typeAdapterOf(concrete), taCache.typeAdapterOf(fallbackType) )
        }
    }.toList

    TypeAdapterCache(
      this,
      parseOrElseFactories ::: permissives ::: TypeAdapterCache.StandardFactories
    )

    /* TODO
    val staged = parseOrElseFactories ::: permissives ::: intermediateContext.factories.toList
    intermediateContext.copy(factories = NonEmptyList(staged.head, staged.tail))
    */

  final inline def read[T](input: WIRE): T =
    val typeAdapter = taCache.typeAdapterOf[T]
    _read(input, typeAdapter)

  def _read[T](input: WIRE, typeAdapter: TypeAdapter[T]): T

  final inline def render[T](t: T): WIRE =
    val typeAdapter = taCache.typeAdapterOf[T]
    _render(t, typeAdapter)

  def _render[T](t: T, typeAdapter: TypeAdapter[T]): WIRE

  // These is so pervasively handy, let's just pre-stage it for easy access
  lazy val stringTypeAdapter: TypeAdapter[String] = taCache.typeAdapterOf[String]
  lazy val anyTypeAdapter: TypeAdapter[Any]       = taCache.typeAdapterOf[Any]
  lazy val anyMapKeyTypeAdapter: TypeAdapter[Any] = taCache.typeAdapterOf[Any]

  // TODO
  // Look up any custom hint label for given type, and if none then use default
  // def getHintLabelFor(tpe: TypeStructure): String = hintMap.getOrElse(tpe, defaultHint)

  def stringWrapTypeAdapterFactory[T](
      wrappedTypeAdapter: TypeAdapter[T],
      emptyStringOk: Boolean = true
    ): TypeAdapter[T]

  def enumsAsInts(): JackFlavor[WIRE]
  def allowPermissivePrimitives(): JackFlavor[WIRE]
  def parseOrElse(poe: (RType, RType)*): JackFlavor[WIRE]
  // def withAdapters(ta: TypeAdapterFactory*): JackFlavor[WIRE]
  // def withDefaultHint(hint: String): JackFlavor[WIRE]
  // def withHints(h: (Type, String)*): JackFlavor[WIRE]
  // def withHintModifiers(hm: (Type, HintValueModifier)*): JackFlavor[WIRE]
  def withTypeValueModifier(tm: HintValueModifier): JackFlavor[WIRE]

  // Need WIRE-specific Builder instance.  By default this is StringBuilder.  Mongo will overwrite this.
  def getBuilder: mutable.Builder[WIRE, WIRE] =
    StringBuilder()
      .asInstanceOf[mutable.Builder[WIRE, WIRE]]

package co.blocke.dottyjack
package json

import model._
import co.blocke.dotty_reflection.RType
import typeadapter.StringWrapTypeAdapter
// import typeadapter.{AnyMapKeyTypeAdapter, StringWrapTypeAdapter}

opaque type JSON = String

case class JsonFlavor(
  override val defaultHint:        String                        = "_hint",
  override val permissivesOk:      Boolean                       = false,
  override val customAdapters:     List[TypeAdapterFactory]      = List.empty[TypeAdapterFactory],
  override val hintMap:            Map[RType, String]            = Map.empty[RType, String],
  override val hintValueModifiers: Map[RType, HintValueModifier] = Map.empty[RType, HintValueModifier],
  override val typeValueModifier:  HintValueModifier             = DefaultHintModifier,
  override val parseOrElseMap:     Map[RType, RType]             = Map.empty[RType, RType],
  override val enumsAsInt:         Boolean                       = false
) extends JackFlavor[JSON] {

  def _read[T](input: JSON, typeAdapter: TypeAdapter[T]): T =
    val parser = JsonParser(input, this)
    typeAdapter.read(parser).asInstanceOf[T]

  def _render[T](t: T, typeAdapter: TypeAdapter[T]): JSON =
    val sb = co.blocke.dottyjack.model.StringBuilder[JSON]()
    typeAdapter.write(t, writer, sb)
    sb.result()

  def parse(input: JSON): Parser = JsonParser(input, this)

  private val writer = JsonWriter() 

  override val stringifyMapKeys: Boolean = true

  def allowPermissivePrimitives(): JackFlavor[JSON] =
    this.copy(permissivesOk = true)
  def enumsAsInts(): JackFlavor[JSON] = this.copy(enumsAsInt = true)
  def parseOrElse(poe: (RType, RType)*): JackFlavor[JSON] =
    this.copy(parseOrElseMap = this.parseOrElseMap ++ poe)
  def withAdapters(ta: TypeAdapterFactory*): JackFlavor[JSON] =
    this.copy(customAdapters = this.customAdapters ++ ta.toList)
  def withDefaultHint(hint: String): JackFlavor[JSON] =
    this.copy(defaultHint = hint)
  def withHints(h: (RType, String)*): JackFlavor[JSON] =
    this.copy(hintMap = this.hintMap ++ h)
  def withHintModifiers(hm: (RType, HintValueModifier)*): JackFlavor[JSON] =
    this.copy(hintValueModifiers = this.hintValueModifiers ++ hm)
  def withTypeValueModifier(tm: HintValueModifier): JackFlavor[JSON] =
    this.copy(typeValueModifier = tm)

  def stringWrapTypeAdapterFactory[T](
      wrappedTypeAdapter: TypeAdapter[T],
      emptyStringOk:      Boolean        = true,
      maybe:              Boolean        = false
  ): TypeAdapter[T] =
    StringWrapTypeAdapter(wrappedTypeAdapter, emptyStringOk, maybe)
}

package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.TypeMemberInfo
import co.blocke.dotty_reflection.info._
import co.blocke.dotty_reflection._

import scala.collection.mutable
// import model.ClassHelper.ExtraFieldValue

// For case classes and Java/Scala plain classes, but not traits
trait ClassTypeAdapterBase[T] extends TypeAdapter[T] with Classish:
  val info:               RType
  val argsTemplate:       Array[Object]
  val fieldBitsTemplate:  mutable.BitSet
  val isSJCapture:        Boolean
  val fieldMembersByName: Map[String, ClassFieldMember[_]]
  val isCaseClass:        Boolean = false


case class CaseClassTypeAdapter[T](
    info:               RType,
    // typeMembersByName:  Map[String, ClassHelper.TypeMember[T]],
    fieldMembersByName: Map[String, ClassFieldMember[_]],
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet,
    typeMembersByName:  Map[String, TypeMemberInfo]
    // dbCollectionName:   Option[String]
)(implicit taCache: TypeAdapterCache) extends ClassTypeAdapterBase[T]:

  override val isCaseClass = true;

  private val classInfo = info.asInstanceOf[ScalaClassInfo]
  val orderedFieldNames = classInfo.fields.map(_.name)

  val isSJCapture = classInfo.hasMixin("co.blocke.dottyjack.SJCapture")

  def read(parser: Parser): T =
    if (parser.peekForNull) then
      null.asInstanceOf[T]
    else 
      // External type hint --> Substitute type field's type into the placeholder (i.e.'T') in the class' fields
      val (foundBits, args, captured) = {
        if (classInfo.typeMembers.nonEmpty) then
          val fixedFields = findActualTypeMemberTypes(parser)  // Resolve actual type of type member (should be a class) and substitute any fields having that type with the actual
          val substitutedClassInfo = this.copy(fieldMembersByName = fixedFields) //, info = info.asInstanceOf[ScalaClassInfo].setActualTypeParams( classInfo.typeMembers.map(m => parserFound(m.name))))
          parser.expectObject(substitutedClassInfo, taCache.jackFlavor.defaultHint)
        else
          parser.expectObject(this, taCache.jackFlavor.defaultHint)
      }

      if (foundBits.isEmpty) then
        val asBuilt = 
          if (classInfo.typeMembers.nonEmpty) then
            val originalArgTypes = classInfo.fields.map(_.fieldType.infoClass)
            val const = classInfo.infoClass.getConstructors.head // <-- NOTE: head here isn't bullet-proof, but a generally safe assumption for case classes.  (Req because of arg typing mess.)
            const.newInstance(args:_*).asInstanceOf[T]
          else
            classInfo.constructWith[T](args)
        if isSJCapture
          asBuilt.asInstanceOf[SJCapture].captured = captured
        asBuilt
      else
        parser.backspace()
        throw new ScalaJackError(
          parser.showError(
            s"Class ${classInfo.name} missing required fields: " + foundBits
              .map(b => orderedFieldNames(b))
              .mkString(", ")
          )
        )


  def write[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit = 
    val extras = classInfo.typeMembers.map { tpeMember =>
      (
        tpeMember.name,
        ExtraFieldValue(
          tpeMember.asInstanceOf[TypeMemberInfo].memberType.name,
          // taCache.jackFlavor.typeValueModifier.unapply(tm.baseType),  // TODO
          taCache.jackFlavor.stringTypeAdapter
        )
      )
    }
    writer.writeObject(
      t,
      orderedFieldNames,
      fieldMembersByName,
      out,
      extras.toList
    )

  // Used by AnyTypeAdapter to insert type hint (not normally needed) into output so object
  // may be reconstituted on read
  def writeWithHint[WIRE](
      jackFlavor: JackFlavor[WIRE],
      t:          T,
      writer:     Writer[WIRE],
      out:        mutable.Builder[WIRE, WIRE]): Unit = 
    val hintValue = t.getClass.getName
    val hintLabel = jackFlavor.defaultHint   // TODO  --> .getHintLabelFor(info.name)
    val extra = List(
      (
        hintLabel,
        ExtraFieldValue(hintValue, jackFlavor.stringTypeAdapter)
      )
    )
    writer.writeObject(t, orderedFieldNames, fieldMembersByName, out, extra)

  // Use parser to scan JSON for type member name and materialize the TypeMemberInfo with RType of actual/found type.
  // (The original TypeMember's RType is likely a trait.  The parser-found type should be a concrete class (ScalaClassInfo).)
  private def findActualTypeMemberTypes(
      parser:  Parser
  ): Map[String, ClassFieldMember[_]] = 
    val foundByParser: Map[String, TypeMemberInfo] = parser.resolveTypeMembers(
      typeMembersByName,
      taCache.jackFlavor.typeValueModifier
    )
    if (typeMembersByName.size != foundByParser.size)
      throw new ScalaJackError(
        parser.showError(
          "Did not find required type member(s): " + typeMembersByName.keySet
            .diff(foundByParser.keySet.map(_.toString))
            .mkString(",")
        )
      )

    // Map[TypeSymbol,TypeSymbolInfo] 
    val invertedBySymbol = foundByParser.map( (name, tpeInfo) => (tpeInfo.typeSymbol, tpeInfo))

    fieldMembersByName.map {
      case (name, fm) if fm.info.originalSymbol.flatMap(s => invertedBySymbol.get(s)).isDefined =>
        val actualTypeAdapter = taCache.typeAdapterOf(invertedBySymbol(fm.info.originalSymbol.get).memberType) // get TypeAdapter for actual type
        // handle wrapped types (Option, Fallback, Collections, etc.)
        // NOTE: This is sub-optimal and not "deep".  Need a better solution to sew past Level-0
        val fixedTypeAdapter = fm.valueTypeAdapter match {
          // TODO
          // case fallback: FallbackTypeAdapter[_, _] =>
          //   FallbackTypeAdapter(
          //     fallback.taCache,
          //     Some(actualTypeAdapter.asInstanceOf[TypeAdapter[Any]]),
          //     fallback.orElseType
          //   )
          case op: OptionTypeAdapter[_] => op.copy(valueTypeAdapter = actualTypeAdapter)
          // TODO: EitherTypeAdapter
          // TODO: ArrayTypeAdapter
          // TODO: Collections...
          // TODO: TupleTypeAdapter
          // TODO: UnionTypeAdapter
          // TODO: IntersectionTypeAdapter
          // TODO: Java TypeAdapters
          case _ => actualTypeAdapter
        }
        (name, fm.copy(info = fm.info.asInstanceOf[ScalaFieldInfo].copy(fieldType = invertedBySymbol(fm.info.originalSymbol.get)), valueTypeAdapter = fixedTypeAdapter))
      case (name, fm) => (name, fm)
    }


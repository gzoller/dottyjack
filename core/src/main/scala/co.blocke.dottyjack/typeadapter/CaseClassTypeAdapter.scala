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
  val orderedFieldNames = info.asInstanceOf[ClassInfo].fields.map{ f => 
    // Re-map field names if @Change annotation is present
    f.annotations.get("co.blocke.dottyjack.Change").map(_("name")).getOrElse(f.name)
    }


case class CaseClassTypeAdapter[T](
    info:               RType,
    fieldMembersByName: Map[String, ClassFieldMember[_]],
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet,
    typeMembersByName:  Map[String, TypeMemberInfo]
    // dbCollectionName:   Option[String]
)(implicit taCache: TypeAdapterCache) extends ClassTypeAdapterBase[T]:

  override val isCaseClass = true;

  private val classInfo = info.asInstanceOf[ScalaClassInfo]
  // val orderedFieldNames = classInfo.fields.map(_.name)

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
          val const = classInfo.infoClass.getConstructors.head // <-- NOTE: head here isn't bullet-proof, but a generally safe assumption for case classes.  (Req because of arg typing mess.)
          if (classInfo.typeMembers.nonEmpty) then
            val originalArgTypes = classInfo.fields.map(_.fieldType.infoClass)
            const.newInstance(args:_*).asInstanceOf[T]
          else
            const.newInstance(args:_*).asInstanceOf[T]
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

    // Resolve actual types (in t) of any type members
    val filteredTypeMembers = classInfo.filterTraitTypeParams.typeMembers
    val (extras, resolvedFieldMembersByName) =
      if filteredTypeMembers.nonEmpty then
        val xtras = filteredTypeMembers.map{ tm =>
          val foundActualField = classInfo.fields.find( _.asInstanceOf[ScalaFieldInfo].originalSymbol == Some(tm.typeSymbol) )
          val resolvedTypeMember = foundActualField.map{ a => 
            val actualRtype = Reflector.reflectOnClass(a.valueAccessor.invoke(t).getClass)
            tm.copy(memberType = actualRtype)
          }.getOrElse(tm)
          (
            resolvedTypeMember.name,
            ExtraFieldValue(
              taCache.jackFlavor.typeValueModifier.unapply(resolvedTypeMember.asInstanceOf[TypeMemberInfo].memberType.name),
              taCache.jackFlavor.stringTypeAdapter
            )
          )
        }

        val filteredTypeMemberSymbols = filteredTypeMembers.map(_.typeSymbol)
        val resolvedFields = fieldMembersByName.map{ case (fname, aField) =>        
          val aScalaField = aField.info.asInstanceOf[ScalaFieldInfo]
          if aScalaField.originalSymbol.isDefined && filteredTypeMemberSymbols.contains(aScalaField.originalSymbol.get) then
            val actualRtype = Reflector.reflectOnClass(aScalaField.valueAccessor.invoke(t).getClass)
            fname -> aField.copy( info = aScalaField.copy( fieldType = actualRtype ), valueTypeAdapter = taCache.typeAdapterOf(actualRtype) )
          else
            fname -> aField
        }

        (xtras, resolvedFields)
      else
        (Nil, fieldMembersByName)

    writer.writeObject(
      t,
      orderedFieldNames,
      resolvedFieldMembersByName,
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
    // Filter any non-trait/class type members... we ignore these so they don't mess up type hint modifiers
    val filtered = typeMembersByName.collect {
      case (k,tm) if tm.memberType.isInstanceOf[TraitInfo] || tm.memberType.isInstanceOf[ScalaClassInfo] => (k,tm)
    }
    if (filtered.size != foundByParser.size)
      throw new ScalaJackError(
        parser.showError(
          "Did not find required type member(s): " + typeMembersByName.keySet
            .diff(foundByParser.keySet.map(_.toString))
            .mkString(",")
        )
      )

    // Map[TypeSymbol,TypeSymbolInfo] 
    val invertedBySymbol = foundByParser.map( (name, tpeInfo) => (tpeInfo.typeSymbol, tpeInfo))

    // NOTE: This is sub-optimal and not "deep".  Need a better solution to sew past Level-1 for a general n-deep solution
    // As it stands, reflection doesn't provide a way to "sew" types deeply (dynamically).
    fieldMembersByName.map {
      case (name, fm) if fm.info.originalSymbol.flatMap(s => invertedBySymbol.get(s)).isDefined =>
        val actualTypeAdapter = taCache.typeAdapterOf(invertedBySymbol(fm.info.originalSymbol.get).memberType) // get TypeAdapter for actual type
        val fixedTypeAdapter = fm.valueTypeAdapter match {
          case fallback: FallbackTypeAdapter[_, _] =>
            FallbackTypeAdapter(
              actualTypeAdapter.asInstanceOf[TypeAdapter[Any]],
              fallback.orElseTypeAdapter.asInstanceOf[TypeAdapter[Any]]
            )
          case _ => actualTypeAdapter
        }
        (name, fm.copy(info = fm.info.asInstanceOf[ScalaFieldInfo].copy(fieldType = invertedBySymbol(fm.info.originalSymbol.get)), valueTypeAdapter = fixedTypeAdapter))
      case (name, fm) =>
        (name, fm)
    }


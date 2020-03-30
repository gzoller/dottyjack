package co.blocke.dottyjack
package typeadapter

import model._
import co.blocke.dotty_reflection.infos._

import scala.collection.mutable
// import model.ClassHelper.ExtraFieldValue

// For case classes and Java/Scala plain classes, but not traits
trait ClassTypeAdapterBase[T] extends TypeAdapter[T] with Classish:
  val info:               ConcreteType
  val argsTemplate:       Array[Object]
  val fieldBitsTemplate:  mutable.BitSet
  val isSJCapture:        Boolean
  val fieldMembersByName: Map[String, ClassFieldMember[_]]
  val isCaseClass:        Boolean = false


case class CaseClassTypeAdapter[T](
    info:               ConcreteType,
    // className:          String,
    // typeMembersByName:  Map[String, ClassHelper.TypeMember[T]],
    // orderedFieldNames:  List[String],
    // fieldMembersByName: Map[String, ClassHelper.ClassFieldMember[T, Any]],
    argsTemplate:       Array[Object],
    fieldBitsTemplate:  mutable.BitSet
    // constructorMirror:  MethodMirror,
    // isSJCapture:        Boolean,
    // dbCollectionName:   Option[String]
)(implicit taCache: TypeAdapterCache) extends ClassTypeAdapterBase[T]:

  override val isCaseClass = true;

  private val classInfo = info match {
    case c: ScalaClassInfo => c 
    case _ => throw new ScalaJackError("Unexpected info type: "+info.getClass.getName)
  }
  private val orderedFieldNames = classInfo.fields.map(_.name)

  val fieldMembersByName = 
    classInfo.fields.map{f => 
      val fieldMember: ClassFieldMember[_] = f.fieldType match {
        case c: ConcreteType => 
          ClassFieldMember(
            f,
            taCache.typeAdapter(c),
            None,  // TODO
            None,  // TODO
            None   // TODO
          )
        case c => throw new ScalaJackError(s"Concrete type expected for class ${info.name} field ${f.name}.  ${c.getClass.getName} was found.")
      }
      f.name -> fieldMember}.toMap
  val isSJCapture = classInfo.hasMixin("co.blocke.dottyjack.SJCapture")

  def read(parser: Parser): T =
    if (parser.peekForNull) then
      null.asInstanceOf[T]
    else 
      // External type hint --> Substitute type field's type into the placeholder (i.e.'T') in the class' fields
      val substitutedTypeMembersIfAny =
        /*
        if (typeMembersByName.nonEmpty) {
          val fixedFields = substituteTypeMemberTypes(parser, taCache)
          this.copy(fieldMembersByName = fixedFields)
        } else
          */
          this // No type members in this class... do nothing

      val (foundBits, args, captured) = parser.expectObject(
        substitutedTypeMembersIfAny,
        taCache.jackFlavor.defaultHint
      )
      if (foundBits.isEmpty) then
        val asBuilt = classInfo.constructWith[T](args)
        // val asBuilt = classInfo.constructWith[T](args.toIndexedSeq.map(_.asInstanceOf[Object]): _*)
          // constructorMirror.apply(args.toIndexedSeq: _*).asInstanceOf[T]
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
    val extras = Nil
    /*
    val extras = typeMembersByName
      .map {
        case (typeMemberName, tm) =>
          (
            typeMemberName,
            ExtraFieldValue(
              taCache.jackFlavor.typeValueModifier.unapply(tm.baseType),
              taCache.jackFlavor.stringTypeAdapter
            )
          )
      }
      */
    writer.writeObject(
      t,
      orderedFieldNames,
      fieldMembersByName,
      out,
      extras.toList
    )

  // Used by AnyTypeAdapter to insert type hint (not normally needed) into output so object
  // may be reconstituted on read
  /*
  def writeWithHint[WIRE](
      t:      T,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit = {
    val hintValue = t.getClass.getName
    val hintLabel = taCache.jackFlavor.getHintLabelFor(tt.tpe)
    val extra = List(
      (
        hintLabel,
        ClassHelper
        .ExtraFieldValue(hintValue, taCache.jackFlavor.stringTypeAdapter)
      )
    )
    writer.writeObject(t, orderedFieldNames, fieldMembersByName, out, extra)
  }
*/
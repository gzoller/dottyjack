package co.blocke.dottyjack
package typeadapter

import model._
import classes._
import co.blocke.dotty_reflection._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Success, Try}

object AnyTypeAdapterFactory extends TypeAdapterFactory:
  def matches(concrete: RType): Boolean = 
    concrete match {
      case PrimitiveType.Scala_Any => true
      case _ => false
    }

  def makeTypeAdapter(concrete: RType)(implicit taCache: TypeAdapterCache): TypeAdapter[_] =  
    AnyTypeAdapter(concrete, taCache)


case class AnyTypeAdapter(info: RType, taCache: TypeAdapterCache) extends TypeAdapter[Any] {
  val jackFlavor = taCache.jackFlavor
  lazy val mapAnyTypeAdapter: TypeAdapter[Map[Any, Any]]  = taCache.typeAdapterOf[Map[Any, Any]]
  lazy val listAnyTypeAdapter: TypeAdapter[List[Any]]     = taCache.typeAdapterOf[List[Any]]
  lazy val optionAnyTypeAdapter: TypeAdapter[Option[Any]] = taCache.typeAdapterOf[Option[Any]]

  def read(parser: Parser): Any =
    parser match {
      case p if p.peekForNull   => null
      case p if p.nextIsBoolean => p.expectBoolean()
      case p if p.nextIsNumber  =>
        BigDecimal(p.expectNumber()) match {
          case i if i.isValidInt      => i.toIntExact
          case i if i.isValidLong     => i.toLongExact
          case d if d.isDecimalDouble => d.toDouble
          case d if d.ulp == 1        => d.toBigInt
          case d                      => d
        }
      case p if p.nextIsString && jackFlavor.permissivesOk =>
        jackFlavor.stringWrapTypeAdapterFactory(this).read(p)
      case p if p.nextIsString => p.expectString()
      case p if p.nextIsArray =>
        val listBuilder: ListBuffer[Any] = mutable.ListBuffer.empty[Any]
        p.expectList(jackFlavor.anyTypeAdapter, listBuilder)
      case p if p.nextIsObject =>
        val mapBuilder = mutable.Map
          .empty[Any, Any]
          .asInstanceOf[mutable.Builder[(Any, Any), mutable.Map[Any, Any]]]
        val mark = parser.mark()
        val foundMap = p.expectMap[Any, Any, mutable.Map[Any, Any]](
          jackFlavor.stringWrapTypeAdapterFactory(this),
          this,
          mapBuilder
        )
        if (foundMap.contains(jackFlavor.defaultHint)) {
          parser.revertToMark(mark)
          Try(
            Class.forName(foundMap(jackFlavor.defaultHint).toString)
          ) match {
            case Success(concreteTypeClass) => taCache.typeAdapterOf(Reflector.reflectOnClass(concreteTypeClass)).read(p)
            case _ => foundMap
          }
        } else
          foundMap.toMap
      case p => p.sourceAsString
    }

  // Need this little bit of gymnastics here to unpack the X type parameter so we can use it to case the TypeAdapter
  private def unpack[X, WIRE](value: X, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    taCache
      .typeAdapterOf(Reflector.reflectOnClass(value.getClass))
      .asInstanceOf[TypeAdapter[X]] match {
        case ta: CaseClassTypeAdapter[X] =>
          val builder = jackFlavor.getBuilder.asInstanceOf[mutable.Builder[WIRE, WIRE]]
          ta.writeWithHint[WIRE](jackFlavor.asInstanceOf[JackFlavor[WIRE]], value, writer, builder)
          writer.writeRaw(builder.result(), out)
        case ta: NonCaseClassTypeAdapter[X] =>
          val builder = jackFlavor.getBuilder.asInstanceOf[mutable.Builder[WIRE, WIRE]]
          ta.writeWithHint[WIRE](jackFlavor.asInstanceOf[JackFlavor[WIRE]], value, writer, builder)
          writer.writeRaw(builder.result(), out)
        case ta => 
          ta.write(value, writer, out)
      }


  // WARNING: JSON output broken for Option[...] where value is None -- especially bad for Map keys!
  def write[WIRE](
      t:      Any,
      writer: Writer[WIRE],
      out:    mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      case null         => writer.writeNull(out)
      case e if e.getClass.getName =="scala.Enumeration$Val" => writer.writeString(t.toString, out)
      case _: scala.Enum => writer.writeString(t.toString, out)
      case _: Map[_, _] => mapAnyTypeAdapter.write(t.asInstanceOf[Map[Any, Any]], writer, out)
      case _: Seq[_]    => listAnyTypeAdapter.write(t.asInstanceOf[List[Any]], writer, out)
      case _: Option[_] => optionAnyTypeAdapter.write(t.asInstanceOf[Option[Any]], writer, out)
      case v            => unpack[Any,WIRE](v, writer, out)
    }
}


// For stringified Map keys, i.e. JSON.  If value is a string, handle normally, else treat as a string wrapper
case class AnyMapKeyTypeAdapter(
    taCache: TypeAdapterCache
  ) extends TypeAdapter[Any]:

  val info: RType = Reflector.reflectOn[Any]
  val jackFlavor = taCache.jackFlavor
  val anyTA: TypeAdapter[Any] = taCache.typeAdapterOf[Any]

  lazy val mapAnyTypeAdapter: TypeAdapter[Map[Any, Any]]  = taCache.typeAdapterOf[Map[Any, Any]]
  lazy val listAnyTypeAdapter: TypeAdapter[List[Any]]     = taCache.typeAdapterOf[List[Any]]
  lazy val optionAnyTypeAdapter: TypeAdapter[Option[Any]] = taCache.typeAdapterOf[Option[Any]]

  def read(parser: Parser): Any =
    parser.expectString() match {
      case null => null
      case s    => anyTA.read(parser.subParser(s.asInstanceOf[parser.WIRE]))
    }
  
  private def unpack[X, WIRE](
      value: X,
      writer: Writer[WIRE],
      out: mutable.Builder[WIRE, WIRE],
      isMapKey: Boolean
    ): Unit = 
      taCache.typeAdapterOf(Reflector.reflectOnClass(value.getClass)) match {
      case ta: Stringish => ta.asInstanceOf[TypeAdapter[X]].write(value, writer, out)
      case ta: CaseClassTypeAdapter[_] =>
        val builder = jackFlavor.getBuilder.asInstanceOf[mutable.Builder[Any, WIRE]]
        ta.asInstanceOf[CaseClassTypeAdapter[X]].writeWithHint[WIRE](
          jackFlavor.asInstanceOf[JackFlavor[WIRE]],
          value,
          writer,
          builder
        )
        writer.writeString(builder.result().toString, out)
      case ta =>
        jackFlavor.stringWrapTypeAdapterFactory(ta.asInstanceOf[TypeAdapter[X]]).write(value, writer, out)
    }

  // WARNING: JSON output broken for Option[...] where value is None -- especially bad for Map keys!
  def write[WIRE](t: Any, writer: Writer[WIRE], out: mutable.Builder[WIRE, WIRE]): Unit =
    t match {
      // Null not needed: null Map keys are inherently invalid in SJ
      case e if e.getClass.getName =="scala.Enumeration$Val" => writer.writeString(t.toString, out)
      case _: scala.Enum => writer.writeString(t.toString, out)
      case _: Map[_, _] =>
        jackFlavor
          .stringWrapTypeAdapterFactory(mapAnyTypeAdapter)
          .write(t.asInstanceOf[Map[Any, Any]], writer, out)
      case _: Seq[_] =>
        jackFlavor
          .stringWrapTypeAdapterFactory(listAnyTypeAdapter)
          .write(t.asInstanceOf[List[Any]], writer, out)
      case opt: Option[_] if opt.isDefined =>
        write(t.asInstanceOf[Option[_]].get, writer, out)
      // $COVERAGE-OFF$Should be impossible (Nones filtered by CanBuildFromTypeAdapter).  Code left in as a safety
      case opt: Option[_] if opt.isEmpty =>
        optionAnyTypeAdapter.write(None, writer, out)
      // $COVERAGE-ON$
      case v =>
        unpack(t, writer, out, isMapKey = true)
    }
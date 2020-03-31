package co.blocke.dottyjack
package json

import model._
import scala.collection.mutable
// import ClassHelper.ExtraFieldValue
import scala.collection.Map
// import typeadapter.TupleTypeAdapterFactory

case class JsonWriter() extends Writer[JSON] {

  @inline def addString(s: String, out: mutable.Builder[JSON, JSON]): Unit =
    out += s.asInstanceOf[JSON]

  def writeArray[Elem](t: Iterable[Elem], elemTypeAdapter: TypeAdapter[Elem], out: mutable.Builder[JSON, JSON]): Unit = t match {
    case null => addString("null", out)
    case a =>
      out += "[".asInstanceOf[JSON]
      val iter = a.iterator
      while (iter.hasNext) {
        elemTypeAdapter.write(iter.next, this, out)
        if (iter.hasNext)
          out += ",".asInstanceOf[JSON]
      }
      out += "]".asInstanceOf[JSON]
  }

  def writeBigInt(t: BigInt, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.toString, out)

  def writeBoolean(t: Boolean, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.toString, out)

  def writeDecimal(t: BigDecimal, out: mutable.Builder[JSON, JSON]): Unit =
    t match {
      case null => addString("null", out)
      case s    => addString(s.toString, out)
    }

  def writeDouble(t: Double, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.toString, out)

  def writeInt(t: Int, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.toString, out)

  def writeLong(t: Long, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.toString, out)

  def writeMap[Key, Value, To](t: Map[Key, Value], keyTypeAdapter: TypeAdapter[Key], valueTypeAdapter: TypeAdapter[Value], out: mutable.Builder[JSON, JSON]): Unit =
    t match {
      case null => addString("null", out)
      case daMap =>
        out += "{".asInstanceOf[JSON]
        var first = true
        daMap.foreach {
          case (key, value) =>
            if (first)
              first = false
            else
              out += ",".asInstanceOf[JSON]
            if (key == null)
              throw new ScalaJackError("Map keys cannot be null.")
            keyTypeAdapter.write(key, this, out)
            out += ":".asInstanceOf[JSON]
            valueTypeAdapter.write(value, this, out)
        }
        out += "}".asInstanceOf[JSON]
    }

  def writeString(t: String, out: mutable.Builder[JSON, JSON]): Unit =
    t match {
      case null => addString("null", out)
      case _: String =>
        out += "\"".asInstanceOf[JSON]
        var i      = 0
        val length = t.length
        val chars  = t.toCharArray

        while (i < length) {
          chars(i) match {
            case '"'  => addString("""\"""", out)
            case '\\' => addString("""\\""", out)
            case '\b' => addString("""\b""", out)
            case '\f' => addString("""\f""", out)
            case '\n' => addString("""\n""", out)
            case '\r' => addString("""\r""", out)
            case '\t' => addString("""\t""", out)
            case ch if ch < 32 || ch >= 128 =>
              addString("""\""" + "u" + "%04x".format(ch.toInt), out)
            case c => out += c.toString.asInstanceOf[JSON]
          }

          i += 1
        }
        out += "\"".asInstanceOf[JSON]
    }

  def writeRaw(t: JSON, out: mutable.Builder[JSON, JSON]): Unit =
    addString(t.asInstanceOf[String], out)

  def writeNull(out: mutable.Builder[JSON, JSON]): Unit =
    addString("null", out)

  @inline private def writeFields(
      isFirst: Boolean,
      fields: List[(String, Any, TypeAdapter[Any])],
      out: mutable.Builder[JSON, JSON]
  ): Boolean = {
    var first = isFirst
    for ((label, value, valueTypeAdapter) <- fields)
      if (value != None) {
        if (first)
          first = false
        else
          out += ",".asInstanceOf[JSON]
        writeString(label, out)
        out += ":".asInstanceOf[JSON]
        valueTypeAdapter.write(value, this, out)
      }
    first
  }

  def writeObject[T](
      t: T,
      orderedFieldNames: List[String],
      fieldMembersByName: Map[String, ClassFieldMember[_]],
      out: mutable.Builder[JSON, JSON],
      extras: List[(String, ExtraFieldValue[_])]
  ): Unit = {
    if (t == null) {
      addString("null", out)
    } else {
      out += "{".asInstanceOf[JSON]
      val wasFirst = writeFields(
        isFirst = true,
        extras.map(
          e =>
            (
              e._1,
              e._2.value,
              e._2.valueTypeAdapter.asInstanceOf[TypeAdapter[Any]]
          )
        ),
        out
      )
      val wasFirst2 = writeFields(
        wasFirst,
        orderedFieldNames
          .map { fieldName => // Strictly-speaking JSON has no order, but it's clean to write out in constructor order.
            val oneField = fieldMembersByName(fieldName)
            (fieldName, oneField.info.valueAccessor.invoke(t), oneField.valueTypeAdapter.asInstanceOf[TypeAdapter[Any]])
          },
        out
      )
      t match {
        case sjc: SJCapture =>
          import scala.jdk.CollectionConverters._
          var first = wasFirst2
          sjc.captured.asScala.foreach {
            case (field, fvalue) =>
              if (first)
                first = false
              else
                out += ",".asInstanceOf[JSON]
              writeString(field, out)
              out += ":".asInstanceOf[JSON]
              out += fvalue.asInstanceOf[JSON] // all json captured things are String
          }
        case _ =>
      }
      out += "}".asInstanceOf[JSON]
    }
  }

  def writeTuple[T](t: T, writeFns: List[TupleField[_]], out: mutable.Builder[JSON, JSON]): Unit = {
    out += "[".asInstanceOf[JSON]
    var first = true
    writeFns.foreach { f =>
      if (first)
        first = false
      else
        out += ",".asInstanceOf[JSON]
      f.write(t, this, out)
    }
    out += "]".asInstanceOf[JSON]
  }
}

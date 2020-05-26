package co.blocke.dottyjack

import model._
import json._

object DottyJack:
  def apply()                                      = JsonFlavor()
  def apply[S](kind: JackFlavor[S]): JackFlavor[S] = kind


class ScalaJackError(msg: String)                           extends Exception(msg)
class ScalaJackValueError(val value: Any, cause: Throwable) extends Exception(cause.getMessage)

type HintBijective = util.BijectiveFunction[String, String]
val CHANGE_ANNO = "co.blocke.dottyjack.Change"
val OPTIONAL_ANNO = "co.blocke.dottyjack.Optional"
val IGNORE = "co.blocke.dottyjack.Ignore"
val DB_KEY = "co.blocke.dottyjack.DBKey"
val SJ_CAPTURE  = "co.blocke.dottyjack.SJCapture"
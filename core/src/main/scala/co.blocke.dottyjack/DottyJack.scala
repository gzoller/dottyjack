package co.blocke.dottyjack

import model._
import json._

object DottyJack:
  def apply()                                      = JsonFlavor()
  def apply[S](kind: JackFlavor[S]): JackFlavor[S] = kind


class ScalaJackError(msg: String)                           extends Exception(msg)
class ScalaJackValueError(val value: Any, cause: Throwable) extends Exception(cause.getMessage)

type HintBijective = util.BijectiveFunction[String, String]

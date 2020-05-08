package co.blocke.dottyjack

trait SJCapture {
  var captured: java.util.HashMap[String,String] = new java.util.HashMap[String, String]()
}

// Java classes should inherit this!
class SJCaptureJava extends SJCapture

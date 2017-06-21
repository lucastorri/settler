package com.unstablebuild.settler.error

import scala.util.control.NoStackTrace

case class SettlerException(message: String = null, cause: Throwable = null)
    extends Exception(message, cause)
    with NoStackTrace

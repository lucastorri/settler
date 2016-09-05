package com.unstablebuild.settler.model

import java.time.{Duration => JavaDuration}

import scala.language.experimental.macros


case class MemorySize(toBytes: Long) extends Ordered[MemorySize] {

  override def compare(that: MemorySize): Int = toBytes.compareTo(that.toBytes)

}

package com.unstablebuild.settler.model

case class MemorySize(toBytes: Long) extends Ordered[MemorySize] {

  override def compare(that: MemorySize): Int = toBytes.compareTo(that.toBytes)

}

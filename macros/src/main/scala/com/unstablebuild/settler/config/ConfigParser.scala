package com.unstablebuild.settler.config

trait ConfigParser[T] {

  def apply(value: AnyRef): T

}

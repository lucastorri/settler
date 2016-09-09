package com.unstablebuild.settler.parser

class FunctionParser[T](f: AnyRef => T) extends ConfigParser[T] {

  override def apply(value: AnyRef): T = f(value)

}

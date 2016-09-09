package com.unstablebuild.settler.parser

trait ConfigParser[T] { self =>

  def apply(value: AnyRef): T

  def map[T2](f: T => T2): ConfigParser[T2] =
    ConfigParser(value => f(self(value)))

}

object ConfigParser {

  def apply[T](f: AnyRef => T): ConfigParser[T] =
    new FunctionParser(f)

  def string: ConfigParser[String] =
    new FunctionParser[String](_.toString)

}

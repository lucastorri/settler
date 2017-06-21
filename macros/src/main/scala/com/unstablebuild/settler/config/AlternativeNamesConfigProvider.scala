package com.unstablebuild.settler.config

import com.unstablebuild.settler.error.SettlerException
import com.unstablebuild.settler.model.MemorySize

import scala.concurrent.duration.Duration

case class AlternativeNamesConfigProvider(provider: ConfigProvider, options: Seq[String => String])
    extends ConfigProvider {

  override def has(path: String): Boolean = provider.has(path)

  override def durationSeq(path: String): Seq[Duration] = get(provider.durationSeq, path)

  override def config(path: String): ConfigProvider = copy(provider = get(provider.config, path))

  override def stringSeq(path: String): Seq[String] = get(provider.stringSeq, path)

  override def memSizeSeq(path: String): Seq[MemorySize] = get(provider.memSizeSeq, path)

  override def number(path: String): Number = get(provider.number, path)

  override def string(path: String): String = get(provider.string, path)

  override def configSeq(path: String): Seq[ConfigProvider] = get(provider.configSeq, path)

  override def memSize(path: String): MemorySize = get(provider.memSize, path)

  override def boolSeq(path: String): Seq[Boolean] = get(provider.boolSeq, path)

  override def bool(path: String): Boolean = get(provider.bool, path)

  override def duration(path: String): Duration = get(provider.duration, path)

  override def numberSeq(path: String): Seq[Number] = get(provider.numberSeq, path)

  override def obj(path: String): AnyRef = get(provider.obj, path)

  @inline
  private[this] def get[T](f: String => T, name: String): T =
    (name #:: options.toStream.map(_.apply(name)))
      .find(provider.has)
      .map(f)
      .getOrElse(throw SettlerException(s"Could not find value for key $name"))

}

object AlternativeNamesConfigProvider {

  def originalAndDash(provider: ConfigProvider): AlternativeNamesConfigProvider =
    AlternativeNamesConfigProvider(provider, Seq(alternatives.camelToDash))

  object alternatives {

    def camelToDash(path: String): String =
      "(\\p{Upper}|\\d+)".r.replaceAllIn(path, m => s"-${m.matched.toLowerCase}").stripPrefix("-")

  }

}

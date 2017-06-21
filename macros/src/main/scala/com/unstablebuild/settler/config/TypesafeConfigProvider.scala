package com.unstablebuild.settler.config

import java.time.{Duration => JavaDuration}
import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigMemorySize}
import com.unstablebuild.settler.model.MemorySize

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.language.implicitConversions

case class TypesafeConfigProvider(config: Config) extends ConfigProvider {

  override def has(path: String): Boolean = config.hasPath(path)

  override def config(path: String): ConfigProvider = TypesafeConfigProvider(config.getConfig(path))

  override def string(path: String): String = config.getString(path)

  override def memSize(path: String): MemorySize = toMemSize(config.getMemorySize(path))

  override def duration(path: String): Duration = toDuration(config.getDuration(path))

  override def bool(path: String): Boolean = config.getBoolean(path)

  override def number(path: String): Number = config.getNumber(path)

  override def configSeq(path: String): Seq[ConfigProvider] =
    config.getConfigList(path).asScala.map(TypesafeConfigProvider)

  override def durationSeq(path: String): Seq[Duration] = config.getDurationList(path).asScala.map(toDuration)

  override def stringSeq(path: String): Seq[String] = config.getStringList(path).asScala

  override def memSizeSeq(path: String): Seq[MemorySize] = config.getMemorySizeList(path).asScala.map(toMemSize)

  override def boolSeq(path: String): Seq[Boolean] = config.getBooleanList(path).asScala.map(_.booleanValue())

  override def numberSeq(path: String): Seq[Number] = config.getNumberList(path).asScala

  override def obj(path: String): AnyRef = config.getAnyRef(path)

  private def toDuration(d: JavaDuration): Duration =
    Duration(d.toNanos, TimeUnit.NANOSECONDS)

  private def toMemSize(s: ConfigMemorySize): MemorySize =
    MemorySize(s.toBytes)

}

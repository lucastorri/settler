package com.unstablebuild.settler.config

import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import com.unstablebuild.settler.model.MemorySize

import scala.concurrent.duration.Duration
import scala.language.implicitConversions

trait ConfigProvider {

  def has(path: String): Boolean

  def config(path: String): ConfigProvider

  def configSeq(path: String): Seq[ConfigProvider]

  def string(path: String): String

  def stringSeq(path: String): Seq[String]

  def number(path: String): Number

  def numberSeq(path: String): Seq[Number]

  def bool(path: String): Boolean

  def boolSeq(path: String): Seq[Boolean]

  def memSize(path: String): MemorySize

  def memSizeSeq(path: String): Seq[MemorySize]

  def duration(path: String): Duration

  def durationSeq(path: String): Seq[Duration]

  def obj(path: String): AnyRef

}

object ConfigProvider {

  implicit def fromProperties(properties: Properties): ConfigProvider =
    fromConfig(ConfigFactory.parseProperties(properties))

  implicit def fromConfig(config: Config): ConfigProvider =
    AlternativeNamesConfigProvider.originalAndDash(TypesafeConfigProvider(config))

  def fromEnv(): ConfigProvider =
    AlternativeNamesConfigProvider.originalAndScreaming(TypesafeConfigProvider(ConfigFactory.systemEnvironment()))

}

package com.unstablebuild.settler

import java.io.File

import com.typesafe.config.ConfigFactory
import com.unstablebuild.settler.annotation.Key
import com.unstablebuild.settler.error.SettlerException
import com.unstablebuild.settler.parser.ConfigParser
import org.scalatest.{FlatSpec, MustMatchers}

import scala.concurrent.duration._

class SettlerTest extends FlatSpec with MustMatchers {

  implicit val classParser = new ConfigParser[Class[_]] {
    override def apply(value: AnyRef): Class[_] = Class.forName(value.toString)
  }

  val settings =
    Settler.settings[A](
      ConfigFactory.parseString("""
        |int = 3
        |str = "hello"
        |optIn = "I'm here"
        |b = {
        |  anotherInt = 51
        |  customType = java.io.File
        |}
        |list = ["a", "b"]
        |set = [1, 2, 3]
        |pairSeq = [
        |  {
        |    key = "hey"
        |    value = 21
        |  }
        |]
        |pairSet = [
        |  {
        |    key = "hi"
        |    value = 31
        |  }
        |]
        |aNumber = 37
        |anotherName = 1.23
        |duration = 10s
        |camel-case-to-dash-separated = {
        |  camel-case-int = 67
        |}
        """.stripMargin))

  it must "handle integers" in {
    settings.int must be (3)
  }

  it must "handle strings" in {
    settings.str must equal ("hello")
  }

  it must "handle optional values" in {
    settings.optIn must equal (Some("I'm here"))
    settings.optOut must be (None)
  }

  it must "handle other settings" in {
    settings.b.anotherInt must be (51)
  }

  it must "handle lists" in {
    settings.list must equal (Seq("a", "b"))
  }

  it must "handle sets" in {
    settings.set must equal (Set(1, 2, 3))
  }

  it must "allow implemented method" in {
    settings.optInOrElse must equal ("I'm here")
    settings.optOutOrElse must equal ("default")
  }

  it must "handle settings lists" in {
    settings.pairSeq.map(p => p.key -> p.value) must equal (Seq("hey" -> 21))
  }

  it must "handle settings sets" in {
    settings.pairSet.map(p => p.key -> p.value) must equal (Set("hi" -> 31))
  }

  it must "throw an exception if type doesn't match the interface" in {
    a[SettlerException] must be thrownBy {
      println(settings.aNumber)
    }
  }

  it must "convert durations to finite" in {
    settings.duration must equal(10.seconds)
  }

  it must "allow setting the base name" in {
    settings.renamed must equal (1.23)
  }

  it must "try dash separated names" in {
    settings.camelCaseToDashSeparated.camelCaseInt must equal (67)
  }

  it must "allow custom parsers" in {
    settings.b.customType must equal (classOf[File])
  }

}

trait A {

  def int: Int

  def str: String

  def b: B

  def optIn: Option[String]

  def optOut: Option[String]

  def list: Seq[String]

  def set: Set[Int]

  def optInOrElse = optIn.getOrElse("default")

  def optOutOrElse = optOut.getOrElse("default")

  def pairSeq: Seq[C]

  def pairSet: Set[C]

  def aNumber: Boolean

  @Key(name = "anotherName")
  def renamed: Double

  def duration: FiniteDuration

  def camelCaseToDashSeparated: D

}

trait B {

  def anotherInt: Int

  def customType: Class[_]

}

trait C {

  def key: String

  def value: Int

}

trait D {

  def camelCaseInt: Int

}

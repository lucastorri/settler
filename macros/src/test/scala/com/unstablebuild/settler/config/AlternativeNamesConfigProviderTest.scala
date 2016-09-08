package com.unstablebuild.settler.config

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, MustMatchers}

class AlternativeNamesConfigProviderTest extends FlatSpec with MustMatchers {

  val provider = AlternativeNamesConfigProvider.originalAndDash(
    ConfigFactory.parseString("""with-a-number-42 = true"""))

  it must "convert camel case names to dash separated" in {
    provider.bool("withANumber42") must be (true)
    provider.bool("WithANumber42") must be (true)
  }

}

package com.unstablebuild.settler.config

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, MustMatchers}

class AlternativeNamesConfigProviderTest extends FlatSpec with MustMatchers {

  it must "convert camel case names to dash separated" in {

    val provider =
      AlternativeNamesConfigProvider.originalAndDash(ConfigFactory.parseString("""with-a-number-42 = true"""))

    provider.bool("withANumber42") must be(true)
    provider.bool("WithANumber42") must be(true)
  }

  it must "copy the provider for nested values" in {

    val provider = AlternativeNamesConfigProvider.originalAndDash(
      ConfigFactory.parseString("""first-level = { second-level = 51 }""")
    )

    provider.config("firstLevel").number("secondLevel").intValue() must be(51)
  }

}

package com.unstablebuild.settler

import com.unstablebuild.settler.config.ConfigProvider

import scala.language.experimental.macros

object Settler {

  def settings[S](config: ConfigProvider): S = macro Macros.generateImpl[S]

}

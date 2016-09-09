package com.unstablebuild.settler

import java.net.{URI, URL}

package object parser {

  implicit val uriParser: ConfigParser[URI] = ConfigParser.string.map(URI.create)

  implicit val urlParser: ConfigParser[URL] = uriParser.map(_.toURL)

  implicit val classParser: ConfigParser[Class[_]] = ConfigParser.string.map(Class.forName)

}

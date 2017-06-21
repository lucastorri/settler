# settler

`settler` uses macros to generate implementations for your settings traits in Scala. It allows you to have typed configurations for your applications.

It is inspired by [`owner`](https://github.com/lviggiano/owner).


## Usage

Given a configuration file like this:

```
{
  str: "hello world",
  dbs: [
    { key: "k1", value: 123 },
    { key: "k2", value: 321 }
  ]
}
```

and a piece of code like the following:

```scala
import com.unstablebuild.settler._

trait AppSettings {
  def str: String
  def dbs: Set[DBSettings]
  def opt: Option[String]
  def optOrElse: String = opt.getOrElse("default")
}
trait DBSettings {
  def key: String
  def value: Int
}

val settings = Settler.settings[AppSettings](ConfigFactory.parseFile(myFile))
```

`settler` will generate the implementation for your trait and allow you to do this:

```scala
  println(settings.str)                  // hello world

  settings.dbs.foreach(db =>             // k1: 123
    println(s"${db.key}: ${db.value}"))  // k2: 321

  println(settings.opt)                  // None

  println(settings.optOrElse)            // default
```

It can be used out-of-the-box with [Typesafe's Config](https://github.com/typesafehub/config) or [Java Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html). Additionally, you can implement your own `ConfigProvider` and use whatever source you prefer (custom file formats, databases, Redis, HTTP calls, etc).


### Alternative Name

Case necessary, you can modify the key used to retrieve a setting by using the `@Key` annotation as so:

```scala
trait AlternativeName {
  @Key(name = "rightName")
  def wrongName: Int
}
```


### Custom Types

`settler` supports most of the common Scala types, like `Int`, `String`, `ConfigProvider`, `Duration`, `MemorySize`, plus these same types wrapped on `Seq`, `Set`, and/or `Option`.

Whenever your trait define types that are unknown to the library, it will try to find an implicit implementation of `ConfigParser`. `ConfigParser`s allow the functionallity of the library to be expanded and define custom types.

For example:

```scala
trait CustomSettings {
  def customType: Class[_]
}

implicit val classParser = new ConfigParser[Class[_]] {
  override def apply(value: AnyRef): Class[_] = 
    Class.forName(value.toString)
}

val settings = Settler.settings[CustomSettings](
	ConfigFactory.parseString("customType = java.io.File"))

settings.customType == classOf[java.io.File]
```

A few custom parsers are available by importing `com.unstablebuild.settler.parser._`.


### Config vs Settings

Along the documentation and the code you might see the usage of the words Config and Settings. Although they can broadly be considered synonyms, on the scope of this library Config refers to external libraries, like Typesafe's Config, who provide the mechanism of fetching configuration files and so on. On the other hand, Settings are those same configurations loaded and exposed in a way that makes sense to the application domain.


## Install

To use it with [SBT](http://www.scala-sbt.org/), add the following to your `build.sbt` file:

```scala
resolvers += Resolver.sonatypeRepo("public")

libraryDependencies += "com.unstablebuild" %% "settler" % "0.4.3"
```


## Release

```bash
./sbt test macros/test
./sbt publishSigned macros/publishSigned
./sbt sonatypeReleaseAll
```

## Code Format

This project uses [Scalafmt](http://scalameta.org/scalafmt/) for code formatting. This is done with the help of [neo-sbt-scalafmt](https://github.com/lucidsoftware/neo-sbt-scalafmt) SBT plugin:

```bash
./sbt sbt:scalafmt scalafmt test:scalafmt macros/scalafmt macros/test:scalafmt
```

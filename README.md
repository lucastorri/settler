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


## Install

To use it with [SBT](http://www.scala-sbt.org/), add the following to your `build.sbt` file:

```scala
resolvers += Resolver.sonatypeRepo("public")

libraryDependencies += "com.unstablebuild" %% "settler" % "0.3.0"
```


## Release

```bash
./sbt test
./sbt publishSigned
./sbt sonatypeReleaseAll
```

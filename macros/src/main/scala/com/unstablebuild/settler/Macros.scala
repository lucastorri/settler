package com.unstablebuild.settler

import com.unstablebuild.settler.annotation.Renamed
import com.unstablebuild.settler.config.ConfigProvider
import com.unstablebuild.settler.model.MemorySize

import scala.concurrent.duration.Duration
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Macros {

  def generateImpl[T: c.WeakTypeTag](c: blackbox.Context)(config: c.Expr[ConfigProvider]): c.Expr[T] = {
    import c.universe._

    val tpe = weakTypeOf[T]
    val self = typeOf[Macros.type].decl(TermName("generate")).asMethod

    val definitions = tpe.decls.collect {
      case m: MethodSymbol if m.isAbstract =>

        def conf = m.annotations
          .find(_.tree.tpe =:= typeOf[Renamed])
          .map(_.tree)
          .collectFirst {
            case q"new $_(name = $name)" => name.toString
          }
          .getOrElse(m.asMethod.name.toString)

        def extract(base: Type): Tree = base match {

          case t if t =:= typeOf[String] =>
            q"config.string($conf)"
          case t if t =:= typeOf[Seq[String]] =>
            q"config.stringSeq($conf)"
          case t if t =:= typeOf[Set[String]] =>
            q"config.stringSeq($conf).toSet"

          case t if t =:= typeOf[Int] =>
            q"config.number($conf).intValue"
          case t if t =:= typeOf[Seq[Int]] =>
            q"config.numberSeq($conf).map(_.intValue)"
          case t if t =:= typeOf[Set[Int]] =>
            q"config.numberSeq($conf).map(_.intValue).toSet"

          case t if t =:= typeOf[Long] =>
            q"config.number($conf).longValue"
          case t if t =:= typeOf[Seq[Long]] =>
            q"config.numberSeq($conf).map(_.longValue)"
          case t if t =:= typeOf[Set[Long]] =>
            q"config.numberSeq($conf).map(_.longValue).toSet"

          case t if t =:= typeOf[Double] =>
            q"config.number($conf).doubleValue"
          case t if t =:= typeOf[Seq[Double]] =>
            q"config.numberSeq($conf).map(_.doubleValue)"
          case t if t =:= typeOf[Set[Double]] =>
            q"config.numberSeq($conf).map(_.doubleValue).toSet"

          case t if t =:= typeOf[Number] =>
            q"config.number($conf)"
          case t if t =:= typeOf[Seq[Number]] =>
            q"config.numberSeq($conf)"
          case t if t =:= typeOf[Set[Number]] =>
            q"config.numberSeq($conf).toSet"

          case t if t =:= typeOf[Boolean] =>
            q"config.bool($conf)"
          case t if t =:= typeOf[Seq[Boolean]] =>
            q"config.boolSeq($conf)"
          case t if t =:= typeOf[Set[Boolean]] =>
            q"config.boolSeq($conf).toSet"

          case t if t =:= typeOf[MemorySize] =>
            q"config.memSize($conf)"
          case t if t =:= typeOf[Seq[MemorySize]] =>
            q"config.memSizeSeq($conf)"
          case t if t =:= typeOf[Set[MemorySize]] =>
            q"config.memSizeSeq($conf).toSet"

          case t if t =:= typeOf[Duration] =>
            q"config.duration($conf)"
          case t if t =:= typeOf[Seq[Duration]] =>
            q"config.durationSeq($conf)"
          case t if t =:= typeOf[Set[Duration]] =>
            q"config.durationSeq($conf).toSet"

          case t @ TypeRef(_, _, args) if t <:< typeOf[Option[Any]] =>
            q"if (config.has($conf)) Some(${extract(args.head)}) else None"

          case t if t =:= typeOf[ConfigProvider] =>
            q"config.config($conf)"
          case t if t =:= typeOf[Seq[ConfigProvider]] =>
            q"config.configSeq($conf)"

          case t @ TypeRef(_, _, args) if t <:< typeOf[Seq[Any]] && args.head.typeSymbol.isAbstract =>
            q"config.configSeq($conf).map(c => $self[${args.head}](c))"
          case t @ TypeRef(_, _, args) if t.typeSymbol == typeOf[Set[Any]].typeSymbol && args.head.typeSymbol.isAbstract =>
            q"config.configSeq($conf).map(c => $self[${args.head}](c)).toSet"
          case t if t.typeSymbol.isAbstract =>
            q"$self[$t](config.config($conf))"

          case t =>
            throw new Exception(s"Don't know how to handle $t")
        }

        q"""def ${m.name.decodedName.toTermName} : ${m.returnType} =
          try {
            ${c.Expr(extract(m.returnType))}
          } catch {
            case scala.util.control.NonFatal(e) => throw com.unstablebuild.settler.error.SettlerException(cause = e)
          }
        """
    }

    c.Expr[T](
      q"""{
        new $tpe {

          private val config = $config

          ..$definitions

          override def toString: String =
            scala.collection.mutable.StringBuilder.newBuilder
              .append(${tpe.toString})
              .append("(")
              .append(config.toString)
              .append(")")
              .toString

        }
      }""")
  }

  def generate[T](config: ConfigProvider): T = macro generateImpl[T]

}

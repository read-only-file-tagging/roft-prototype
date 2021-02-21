#!/usr/bin/env amm

import ammonite.ops._

interp.repositories() ++= Seq(coursierapi.IvyRepository.of(
  {
    val h = home.toString()
    s"file://$h/.ivy2/local"
  }
))

import $ivy.`roft::roft-prototype:0.1`
import _root_.roft.cli.Tools
import _root_.roft.cli.Tools.WorkingDirectory
import _root_.roft.{Context, TagStore}


@main
def version() = {
  val version = "prototype-0.1"
  println(s"ROFT version $version")
}

@main
def tag(path: String, tags: String*) = {
  implicit val context: Context = Context(pwd.toString())
  implicit val wd: WorkingDirectory = WorkingDirectory(pwd.toString())
  val storePath = pwd / ".roft-store"
  implicit val store: TagStore = TagStore(new java.io.File(storePath.toString()))
  Tools.tag(path, tags: _*)
}

@main
def tags(path: String): Unit = {
  implicit val context: Context = Context(pwd.toString())
  implicit val wd: WorkingDirectory = WorkingDirectory(pwd.toString())
  val storePath = pwd / ".roft-store"
  implicit val store: TagStore = TagStore(new java.io.File(storePath.toString()))
  println(Tools.tagsByFile(path) mkString "\n")
}

@main
def search(query: String*): Unit = {
  implicit val context: Context = Context(pwd.toString())
  implicit val wd: WorkingDirectory = WorkingDirectory(pwd.toString())
  val storePath = pwd / ".roft-store"
  implicit val store: TagStore = TagStore(new java.io.File(storePath.toString()))
  println(Tools.filesByTag(query: _*) mkString "\n")
}

def scriptLocation: String = {
  val relStr = sys.props("sun.java.command").split("\\s+")(1)
  (pwd / ammonite.ops.RelPath(relStr)).toString()
}

@main
def activate() = {
  val q = "\""
  println(s"alias roft=$q$scriptLocation$q")
}
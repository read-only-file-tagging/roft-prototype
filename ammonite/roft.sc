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
def version(): Unit = {
  val version = "prototype-0.1"
  println(s"ROFT version $version")
}

val RoftRoot = "ROFT_ROOT"
val RoftStore = "ROFT_STORE"

implicit val context: Context = {
  val root = sys.env.getOrElse(RoftRoot, pwd.toString())
  Context(root)
}

implicit val wd: WorkingDirectory = WorkingDirectory(pwd.toString())

val storePath = sys.env.getOrElse(RoftStore, (pwd / ".roft-store").toString)
implicit val store: TagStore = {
  TagStore(new java.io.File(storePath))
}

@main
def tag(path: String, tags: String*) = {
  Tools.tag(path, tags: _*)
}

@main
def tags(path: Option[String]): Unit = {
  path.fold(allTags()) {
    p =>
      println(Tools.tagsByFile(p) mkString "\n")
  }
}

@main
def allTags(): Unit = {
  println(Tools.allTags() mkString "\n")
}


@main
def search(query: String*): Unit = {
  println(Tools.filesByTag(query: _*) mkString "\n")
}

def scriptLocation: String = {
  import ammonite.ops.RelPath
  val relStr = sys.props("sun.java.command").split("\\s+")(1)
  (pwd / RelPath(relStr)).toString()
}

@main
def activate(store: Option[String], root: Option[String]): Unit = {
  val finalRoot = root.getOrElse(context.root)
  val finalStorePath = store.getOrElse(storePath)

  val q = "\""
  println(s"alias roft=$q$scriptLocation$q;")
  println(s"export $RoftRoot=$q$finalRoot$q;")
  println(s"export $RoftStore=$q$finalStorePath$q;")
}

@main
def info(): Unit = {
  println(s"Context: $context")
  println(s"Store path: $storePath")
}
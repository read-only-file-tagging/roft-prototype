package roft.cli

import roft.{Context, TagStore}

import java.io.File

object Tools {
  def rescan()(implicit store: TagStore, context: Context, wd: File): Unit = ???
  def allTags()(implicit store: TagStore): Seq[String] = store.allTags.toVector.map(_.toString)
  def tag(path: String, tags: String*)(implicit store: TagStore, context: Context, wd: File): Boolean = ???
  def filesByTag(query: String*)(implicit store: TagStore, context: Context, wd: File): Seq[String] = ???
  def tagsByFile(path: String, tagFilter: String*): Seq[String] = ???
}


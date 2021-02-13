package roft

import java.io.File

trait Tags {
  def version: String
  def root: File
  def allTags: Set[String]
  def filesForTag(tag: String): Set[File]
  def tagsForFile(file: File): Set[String]
  def snapshot: ImmutableTags[_]

  def apply(tag: String): Set[File] = filesForTag(tag)
  def apply(file: File): Set[String] = tagsForFile(file)
}

trait MutableTags extends Tags {
  def +=(fileWithTag: (File, String)): Unit
  def -=(fileWithTag: (File, String)): Unit
  def version_=(version: String): Unit
  def root_=(root: File): Unit
}

trait ImmutableTags[T <: ImmutableTags[T]] extends Tags {
  def +(fileWithTag: (File, String)): T
  def -(fileWithTag: (File, String)): T
  def withVersion(version: String): T
  def withRoot(root: File): T

  override def snapshot: ImmutableTags[T] = this
}

object Tags {
  def read(store: File): Tags = ???
}
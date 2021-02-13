package roft

import roft.GenTags.{Path, Tag}

import java.io.File

trait GenTags {
  def version: String
  def root: Path
  def allTags: Set[Tag]
  def filesForTag(tag: Tag): Set[Path]
  def tagsForFile(file: Path): Set[Tag]
  def snapshot: ImmutableTags[_]

  def apply(tag: Tag): Set[Path] = filesForTag(tag)
  def apply(file: Path): Set[Tag] = tagsForFile(file)
}

object GenTags {
  type Tag = String
  type Path = File
}

trait MutableTags extends GenTags {
  def +=(fileWithTag: (Path, Tag)): Unit
  def -=(fileWithTag: (Path, Tag)): Unit
  def version_=(version: Tag): Unit
  def root_=(root: Path): Unit
}

trait ImmutableTags[T <: ImmutableTags[T]] extends GenTags {
  def +(fileWithTag: (Path, Tag)): T
  def -(fileWithTag: (Path, Tag)): T
  def withVersion(version: Tag): T
  def withRoot(root: Path): T

  override def snapshot: ImmutableTags[T] = this
}
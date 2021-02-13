package roft

import roft.GenTags.{Path, Tag}

trait GenTags[T <: ImmutableTags[T]] {
  def version: String
  def context: Option[Context]
  def allTags: Set[Tag]
  def PathsForTag(tag: Tag): Set[Path]
  def tagsForPath(file: Path): Set[Tag]
  def entries: Iterable[(Tag,Path)] = for {
    tag <- allTags
    path <- PathsForTag(tag)
  } yield tag -> path

  def snapshot: ImmutableTags[T]

  def apply(tag: Tag): Set[Path] = PathsForTag(tag)
  def apply(file: Path): Set[Tag] = tagsForPath(file)
}

object GenTags {
  class Tag(val parts: Seq[String]) {
    def parent: Tag = if (parts.size < 2) {
      Tag.TagRoot
    } else {
      new Tag(parts.init)
    }

    def isChild(tag: Tag): Boolean = this.parts == tag.parts.take(parts.size)
    def isParent(tag: Tag): Boolean = tag.isChild(this)

    override lazy val toString: String = parts mkString "."

    def canEqual(other: Any): Boolean = other.isInstanceOf[Tag]
    override def equals(other: Any): Boolean = other match {
      case that: Tag =>
        (that canEqual this) &&
          parts == that.parts
      case _ => false
    }
    override def hashCode(): Int = parts.hashCode()
  }
  object Tag {
    object TagRoot extends Tag(Vector.empty)
    def apply(args: String*): Tag = new Tag(args.toVector)
    def fromString(s: String): Tag = new Tag(s.split('.').toVector.filter(_.nonEmpty))
  }
  type Path = String
}

trait MutableTags[T <: ImmutableTags[T]] extends GenTags[T] {
  def +=(fileWithTag: (Path, Tag)): Unit
  def -=(fileWithTag: (Path, Tag)): Unit
  def version_=(version: String): Unit
  def context_=(context: Option[Context]): Unit
  def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit
}

trait ImmutableTags[T <: ImmutableTags[T]] extends GenTags[T] {
  def +(fileWithTag: (Path, Tag)): T
  def -(fileWithTag: (Path, Tag)): T
  def withVersion(version: String): T
  def withContext(context: Option[Context]): T

  override def snapshot: ImmutableTags[T] = this
}
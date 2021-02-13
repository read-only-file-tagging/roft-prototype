package roft

import roft.GenTags.{Path, Tag}

trait GenTags[T <: ImmutableTags[T]] {
  def version: String
  def context: Option[Context]
  def allTags: Set[Tag]
  def filesForTag(tag: Tag): Set[Path]
  def tagsForFile(file: Path): Set[Tag]
  def snapshot: ImmutableTags[T]

  def apply(tag: Tag): Set[Path] = filesForTag(tag)
  def apply(file: Path): Set[Tag] = tagsForFile(file)
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
    override def hashCode(): Int = {
      val state = Seq(parts)
      state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
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
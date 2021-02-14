package roft

import roft.GenTags.{Path, Tag}

trait GenTags[T <: ImmutableTags[T]] {
  def version: String
  def allTags: Set[Tag]
  def knownPaths: Set[Path]
  def pathsForTag(tag: Tag): Set[Path]
  def tagsForPath(file: Path): Set[Tag]
  def entries: Iterable[(Path, Tag)] = for {
    tag <- allTags
    path <- pathsForTag(tag)
  } yield path -> tag

  def snapshot: ImmutableTags[T]

  def apply(tag: Tag): Set[Path] = pathsForTag(tag)
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
  def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit

  def ++=(entries: Traversable[(Path, Tag)]): Unit = entries.foreach(this.+=)
  def --=(entries: Traversable[(Path, Tag)]): Unit = entries.foreach(this.-=)
}

trait ImmutableTags[T <: ImmutableTags[T]] extends GenTags[T] {
  def +(fileWithTag: (Path, Tag)): T
  def -(fileWithTag: (Path, Tag)): T
  def withVersion(version: String): T

  def ++(entries: Traversable[(Path, Tag)]): ImmutableTags[T] = entries.foldLeft(this)(_ + _)
  def --(entries: Traversable[(Path, Tag)]): ImmutableTags[T] = entries.foldLeft(this)(_ - _)
  def withoutEntries: ImmutableTags[T] = this -- entries
  override def snapshot: ImmutableTags[T] = this
}

class Register[T <: ImmutableTags[T]](initialValue: ImmutableTags[T]) extends MutableTags[T] {
  private var value = initialValue
  override def +=(fileWithTag: (Path, Tag)): Unit = value += fileWithTag
  override def -=(fileWithTag: (Path, Tag)): Unit = value -= fileWithTag
  override def ++=(entries: Traversable[(Path, Tag)]): Unit = value ++= entries
  override def --=(entries: Traversable[(Path, Tag)]): Unit = value --= entries
  override def version_=(version: String): Unit = value = value.withVersion(version)
  override def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit = {
    value = value.withVersion(immutableTags.version).withoutEntries ++ immutableTags.entries
  }
  override def version: String = value.version
  override def allTags: Set[Tag] = value.allTags
  override def knownPaths: Set[Path] = value.knownPaths
  override def pathsForTag(tag: Tag): Set[Path] = value.pathsForTag(tag)
  override def tagsForPath(file: Path): Set[Tag] = value.tagsForPath(file)
  override def snapshot: ImmutableTags[T] = value
}

class AtomicRegister[T <: ImmutableTags[T]](initialValue: ImmutableTags[T]) extends MutableTags[T] {

  import java.util.concurrent.atomic.AtomicReference

  private val value = new AtomicReference(initialValue)
  override def +=(fileWithTag: (Path, Tag)): Unit = value.getAndUpdate(_ + fileWithTag)
  override def -=(fileWithTag: (Path, Tag)): Unit = value.getAndUpdate(_ + fileWithTag)
  override def ++=(entries: Traversable[(Path, Tag)]): Unit = value.getAndUpdate(_ ++ entries)
  override def --=(entries: Traversable[(Path, Tag)]): Unit = value.getAndUpdate(_ -- entries)
  override def version_=(version: String): Unit = value.getAndUpdate(_ withVersion version)
  override def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit = {
    value.getAndUpdate(_.withVersion(immutableTags.version).withoutEntries ++ immutableTags.entries)
  }
  override def version: String = value.get().version
  override def allTags: Set[Tag] = value.get().allTags
  override def knownPaths: Set[Path] = value.get().knownPaths
  override def pathsForTag(tag: Tag): Set[Path] = value.get().pathsForTag(tag)
  override def tagsForPath(file: Path): Set[Tag] = value.get().tagsForPath(file)
  override def snapshot: ImmutableTags[T] = value.get()
}
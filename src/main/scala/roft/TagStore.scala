package roft

import roft.GenTags.{Path, Tag}

import java.io.File
import scala.io.Source

class TagStore(val dir: File) extends MutableTags {
  val versionFile = new File(dir, "version")

  override def +=(fileWithTag: (Path, Tag)): Unit = ???
  override def -=(fileWithTag: (Path, Tag)): Unit = ???
  override def version_=(version: Tag): Unit = ???
  override def root_=(root: Path): Unit = ???
  override def version: String = {
    val source = Source.fromFile(versionFile)
    val result = source.mkString.trim
    source.close()
    result
  }
  override def root: Path = ???
  override def allTags: Set[Tag] = ???
  override def filesForTag(tag: Tag): Set[Path] = ???
  override def tagsForFile(file: Path): Set[Tag] = ???
  override def snapshot: ImmutableTags[_] = ???
}

object TagStore {
  def read(store: File): TagStore = new TagStore(store)
}
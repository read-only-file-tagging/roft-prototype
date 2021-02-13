package roft

import ammonite.ops._
import roft.GenTags.{Tag, Path => TagPath}

class TagStore(val dir: Path) extends MutableTags[Tags] {
  def initialized: Boolean = exists(dir)
  val versionFile: Path = dir / "version"

  override def +=(fileWithTag: (TagPath, Tag)): Unit = ???
  override def -=(fileWithTag: (TagPath, Tag)): Unit = ???
  override def version_=(version: String): Unit = {
    write(versionFile, version, createFolders = true)
  }
  override def context_=(context: Option[Context]): Unit = ???
  override def version: String = {
    if (exists(versionFile) && versionFile.isFile) {
      read ! versionFile
    } else {
      ""
    }
  }
  override def context: Option[Context] = None
  override def allTags: Set[Tag] = ???
  override def filesForTag(tag: Tag): Set[TagPath] = ???
  override def tagsForFile(file: TagPath): Set[Tag] = ???
  override def snapshot: Tags = ???
  override def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit = ???
}

object TagStore {
  def apply(store: java.io.File): TagStore = new TagStore(Path(store.getAbsoluteFile))
}
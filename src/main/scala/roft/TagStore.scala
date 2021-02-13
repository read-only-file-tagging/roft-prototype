package roft

import ammonite.ops._
import roft.GenTags.{Tag, Path => TagPath}

class TagStore(val dir: Path) extends MutableTags[Tags] {
  def initialized: Boolean = exists(dir)
  val versionFile: Path = dir / "version"

  override def snapshot: Tags = {
    def readVersion = if (exists(versionFile) && versionFile.isFile) {
      read ! versionFile
    } else {
      ""
    }
    Tags(readVersion)
  }
  override def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit = {
    write(versionFile, immutableTags.version, createFolders = true)
  }

  override def +=(fileWithTag: (TagPath, Tag)): Unit = replaceWith(snapshot + fileWithTag)
  override def -=(fileWithTag: (TagPath, Tag)): Unit = replaceWith(snapshot - fileWithTag)
  override def version_=(version: String): Unit = replaceWith(snapshot.withVersion(version))
  override def context_=(context: Option[Context]): Unit = replaceWith(snapshot.withContext(context))
  override def version: String = snapshot.version
  override def context: Option[Context] = snapshot.context
  override def allTags: Set[Tag] = snapshot.allTags
  override def filesForTag(tag: Tag): Set[TagPath] = snapshot.filesForTag(tag)
  override def tagsForFile(file: TagPath): Set[Tag] = snapshot.tagsForFile(file)
}

object TagStore {
  def apply(store: java.io.File): TagStore = new TagStore(Path(store.getAbsoluteFile))
}
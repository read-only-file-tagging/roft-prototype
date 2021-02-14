package roft

import ammonite.ops._
import roft.GenTags.{Tag, Path => TagPath}

class TagStore(val dir: Path) extends MutableTags[Tags] {
  def initialized: Boolean = exists(dir)
  val versionFile: Path = dir / "version"
  val tagsFile: Path = dir / "tags.tsv"

  override def snapshot: Tags = {
    def readVersion = if (exists(versionFile) && versionFile.isFile) {
      read ! versionFile
    } else {
      ""
    }
    def readEntries = if (exists(tagsFile) && tagsFile.isFile) {
      (read.lines ! tagsFile)
        .map {
          s =>
            val Array(tagStr, pathStr) = s.split('\t')
            Tag.fromString(tagStr) -> pathStr
        }
    } else {
      Seq.empty
    }
    Tags(readVersion, readEntries: _*)
  }
  override def replaceWith[A <: ImmutableTags[A]](immutableTags: ImmutableTags[A]): Unit = {
    write.over(versionFile, immutableTags.version, createFolders = true)
    write.over(tagsFile, immutableTags.entries.map { case (tag, path) => s"$tag\t$path\n" }, createFolders = true)
  }

  override def +=(fileWithTag: (TagPath, Tag)): Unit = replaceWith(snapshot + fileWithTag)
  override def -=(fileWithTag: (TagPath, Tag)): Unit = replaceWith(snapshot - fileWithTag)
  override def version_=(version: String): Unit = replaceWith(snapshot.withVersion(version))
  override def context_=(context: Option[Context]): Unit = replaceWith(snapshot.withContext(context))
  override def version: String = snapshot.version
  override def context: Option[Context] = snapshot.context
  override def allTags: Set[Tag] = snapshot.allTags
  override def PathsForTag(tag: Tag): Set[TagPath] = snapshot.PathsForTag(tag)
  override def tagsForPath(file: TagPath): Set[Tag] = snapshot.tagsForPath(file)
}

object TagStore {
  def apply(store: java.io.File): TagStore = new TagStore(Path(store.getAbsoluteFile))
}
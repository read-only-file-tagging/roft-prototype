package roft.cli

import roft.GenTags.{Path, Tag}
import roft.{Context, ImmutableTags, MutableTags, TagStore}


object Tools {
  case class WorkingDirectory(path: Path)
  object WorkingDirectory {
    def fromFile(file: java.io.File): WorkingDirectory = {
      WorkingDirectory(file.getAbsolutePath)
    }
  }

  def path2storePath(path: Path)(implicit context: Context, wd: WorkingDirectory): Option[Path] = {
    import java.io.File
    val rootInAbsolutePath = new File(context.root).getAbsolutePath
    val fileInAbsolutePath = new File(wd.path, path).getAbsolutePath
    if (fileInAbsolutePath.startsWith(rootInAbsolutePath)) {
      Some(fileInAbsolutePath.substring(rootInAbsolutePath.length))
    } else {
      None
    }
  }

  def rescan[T <: ImmutableTags[T]]()(implicit store: MutableTags[T], context: Context, wd: WorkingDirectory): Unit = ???
  def allTags[T <: ImmutableTags[T]]()(implicit store: MutableTags[T]): Seq[String] = store.allTags.toVector.map(_.toString)
  def tag[T <: ImmutableTags[T]](path: String, tags: String*)(implicit store: MutableTags[T], context: Context, wd: WorkingDirectory): Boolean = {
    val adjustedPath = path2storePath(path)
    adjustedPath.fold(false) {
      path =>
        store ++= tags.map(path -> Tag.fromString(_))
        true
    }
  }
  def filesByTag(query: String*)(implicit store: TagStore, context: Context, wd: WorkingDirectory): Seq[String] = ???
  def tagsByFile(path: String, tagFilter: String*): Seq[String] = ???
}
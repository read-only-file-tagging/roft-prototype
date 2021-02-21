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
    import ammonite.ops.{Path => APath}
    val rootInAbsolutePath = APath(context.root).toString
    val fileInAbsolutePath = if (path.startsWith("/")) {
      path
    } else {
      APath(s"${wd.path}/$path").toString
    }
    if (fileInAbsolutePath.startsWith(rootInAbsolutePath)) {
      Some(fileInAbsolutePath.substring(rootInAbsolutePath.length))
    } else {
      None
    }
  }

  def storePath2path(path: Path)(implicit context: Context, wd: WorkingDirectory): Path = {
    import ammonite.ops.{Path => APath}
    val absolute = APath(s"${wd.path}/$path")
    absolute.relativeTo(APath(context.root)).toString()
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

  def filesByTag[T <: ImmutableTags[T]](query: String*)(implicit store: MutableTags[T], context: Context, wd: WorkingDirectory): Seq[String] =
    if (query.isEmpty) {
      store.entries
        .map(_._1)
        .toSet
        .toVector
        .map(storePath2path)
    } else {
      query
        .map(q => store.pathsForTag(Tag.fromString(q)))
        .reduce(_ intersect _)
        .toVector
        .map(storePath2path)
    }

  def tagsByFile[T <: ImmutableTags[T]](path: String)(implicit store: MutableTags[T], context: Context, wd: WorkingDirectory): Seq[String] =
    path2storePath(path).fold(Seq.empty[String]) {
      sp =>
        store.tagsForPath(sp)
          .toVector
          .map(_.toString)
    }
}
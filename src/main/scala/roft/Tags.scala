package roft

import roft.GenTags.{Path, Tag}

case class Tags(version: String, context: Option[Context] = None, data: Map[Tag, Set[Path]] = Map.empty) extends ImmutableTags[Tags] {
  override def +(fileWithTag: (Path, Tag)): Tags = {
    val (path, tag) = fileWithTag
    val oldPaths = data.getOrElse(tag, Set.empty)
    val newPaths = oldPaths + path
    copy(data = data + (tag -> newPaths))
  }
  override def -(fileWithTag: (Path, Tag)): Tags = {
    val (path, tag) = fileWithTag
    val oldPaths = data.getOrElse(tag, Set.empty)
    val newPaths = oldPaths - path
    if (newPaths.isEmpty) {
      copy(data = data - tag)
    } else {
      copy(data = data + (tag -> newPaths))
    }
  }
  override def withVersion(version: String): Tags = copy(version = version)
  override def withContext(context: Option[Context]): Tags = copy(context = context)

  override def allTags: Set[Tag] = data.keySet
  override def filesForTag(tag: Tag): Set[Path] = data.getOrElse(tag, Set.empty)
  override def tagsForFile(file: Path): Set[Tag] = data.collect {
    case (tag, paths) if paths(file)
    => tag
  }.toSet
}

object Tags
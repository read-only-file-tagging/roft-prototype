package roft

import roft.GenTags.{Path, Tag}

case class Tags(version: String, data: Map[Tag, Set[Path]] = Map.empty) extends ImmutableTags[Tags] {
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

  override def allTags: Set[Tag] = data.keySet
  override def PathsForTag(tag: Tag): Set[Path] = data.getOrElse(tag, Set.empty)
  override def tagsForPath(file: Path): Set[Tag] = data.collect {
    case (tag, paths) if paths(file)
    => tag
  }.toSet
}

object Tags {
  def apply(version: String, entries: (Tag, Path)*): Tags = {
    val map = entries.groupBy(_._1).mapValues(_.map(_._2).toSet)
    new Tags(version, data = map)
  }
}
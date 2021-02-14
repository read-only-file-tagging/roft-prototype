package roft

import roft.GenTags.{Path, Tag}

case class Tags(version: String, data: Set[(Path, Tag)] = Set.empty) extends ImmutableTags[Tags] {
  override def +(fileWithTag: (Path, Tag)): Tags = copy(data = data + fileWithTag)
  override def -(fileWithTag: (Path, Tag)): Tags = copy(data = data - fileWithTag)
  override def withVersion(version: String): Tags = copy(version = version)

  override def allTags: Set[Tag] = data.map(_._2)
  override def knownPaths: Set[Path] = data.map(_._1)
  override def pathsForTag(tag: Tag): Set[Path] = data.collect {
    case (file, `tag`) => file
  }
  override def tagsForPath(file: Path): Set[Tag] = data.collect {
    case (`file`, tag) => tag
  }

  override def withoutEntries: Tags = copy(data = Set.empty)
}

object Tags {
  def apply(version: String, entries: (Path, Tag)*): Tags = new Tags(version, data = entries.toSet)
}
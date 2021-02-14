package roft

import roft.GenTags.Path

case class Context(root: Path)

object Context {
  object Default extends Context("/home")
}
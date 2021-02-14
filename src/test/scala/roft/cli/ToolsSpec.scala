package roft.cli

import org.specs2.mutable.Specification
import roft.GenTags.Tag
import roft.{Context, Register, Tags}
import roft.cli.Tools.WorkingDirectory

import java.io.File

class ToolsSpec extends Specification {
  "Path adjustments" >> {
    "simple example" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("/home/valdis"))
      Tools.path2storePath("abc") must beSome("/valdis/abc")
    }
    "absolute path" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("/home/valdis"))
      Tools.path2storePath("/home/valdis/abc") must beSome("/valdis/abc")
    }
    "relative parent path" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("/home/valdis"))
      Tools.path2storePath("../other_user/abc") must beSome("/other_user/abc")
    }
    "outside the root" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("/home/valdis"))
      Tools.path2storePath("/var/lib") must beNone
    }
    "current path with a dot" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("."))
      Tools.path2storePath("abc").get.contains('·') must beFalse
    }
  }
  "Tagging files" >> {
    "simple example" >> {
      implicit val context: Context = Context.Default
      implicit val wd: WorkingDirectory = WorkingDirectory.fromFile(new File("/home/valdis"))
      implicit val store: Register[Tags] = new Register(Tags("test"))
      Tools.tag("abc", "1", "2", "3")

      store.allTags must_== Set(Tag("1"), Tag("2"), Tag("3"))
      store.tagsForPath("/valdis/abc") must_== Set(Tag("1"), Tag("2"), Tag("3"))
      store.tagsForPath("/valdis/other") must_== Set()

      Tools.allTags().toSet must_== Set("1", "2", "3")
    }
  }
}

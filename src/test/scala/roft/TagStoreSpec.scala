package roft

import org.specs2.mutable.Specification
import roft.GenTags.Tag

import java.io.File
import java.nio.file.Files

class TagStoreSpec extends Specification {
  private def examplesStore(i: Int) = {
    TagStore(new File("src/test/resources/stores/" + i))
  }

  "store 1" >> {
    val store = examplesStore(1)
    "should have the correct versions" >> {
      store.version must_== "0.1"
    }
  }

  "a fresh store" >> {
    val tempDirectory = Files.createTempDirectory("store")
    val store = TagStore(tempDirectory.toFile)

    "have a blank version" >> {
      store.version must_== ""
    }

    "handle version updates" >> {
      val newVersion = "test123"
      store.version must_!= newVersion
      store.version = newVersion
      store.version must_== newVersion
    }

    "handles simple tag updates" >> {
      store.tagsForPath("test") must_== Set()
      store += ("test" -> Tag("abc"))
      store.tagsForPath("test") must_== Set(Tag("abc"))
      store -= ("test" -> Tag("abc"))
      store.tagsForPath("test") must_== Set()
    }

    "handles simple multiple tag updates" >> {
      store += ("one" -> Tag("1"))
      store += ("one" -> Tag("one"))
      store += ("two" -> Tag("1"))
      store += ("two" -> Tag("2"))
      store.tagsForPath("test") must_== Set()

      store.tagsForPath("one") must_== Set(Tag("one"), Tag("1"))
      store.tagsForPath("two") must_== Set(Tag("1"), Tag("2"))

      store("one") must_== Set(Tag("one"), Tag("1"))
      store("two") must_== Set(Tag("1"), Tag("2"))

      store.allTags must_== Set(Tag("one"), Tag("1"), Tag("2"))
      store(Tag("1")) must_== Set("one","two")
      store(Tag("2")) must_== Set("two")
      store(Tag("one")) must_== Set("one")
      store(Tag("nope")) must_== Set()
    }

    "cleanup" >> {
      Files.deleteIfExists(tempDirectory)
    }
  }
}
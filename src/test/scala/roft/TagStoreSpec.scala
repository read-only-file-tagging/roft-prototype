package roft

import org.specs2.mutable.Specification
import roft.GenTags.Tag

import java.io.File
import java.nio.file.Files

class TagStoreSpec extends Specification {
  def examplesStore(i: Int): TagStore = TagStore(new File("src/test/resources/stores/" + i))
  def withExampleStore[T](i: Int)(f: TagStore => T): T = f(examplesStore(i))
  def withTemporaryStore[T](f: TagStore => T): T = {
    val tempDirectory = Files.createTempDirectory("store")
    val store = TagStore(tempDirectory.toFile)
    try f(store) finally {
      ammonite.ops.rm(ammonite.ops.Path(tempDirectory.toAbsolutePath))
    }
  }

  "store 1" >> {
    "should have the correct versions" >> {
      withExampleStore(1) {
        _.version must_== "0.1"
      }
    }
  }

  "a fresh store" >> {
    "have a blank version" >> {
      withTemporaryStore {
        _.version must_== ""
      }
    }

    "handle version updates" >> {
      withTemporaryStore {
        store =>
          val newVersion = "test123"
          store.version must_!= newVersion
          store.version = newVersion
          store.version must_== newVersion
      }
    }

    "handles simple tag updates" >> {
      withTemporaryStore {
        store =>
          store.tagsForPath("test") must_== Set()
          store += ("test" -> Tag("abc"))
          store.tagsForPath("test") must_== Set(Tag("abc"))
          store -= ("test" -> Tag("abc"))
          store.tagsForPath("test") must_== Set()
      }
    }

    "handles simple multiple tag updates" >> {
      withTemporaryStore {
        store =>
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
          store(Tag("1")) must_== Set("one", "two")
          store(Tag("2")) must_== Set("two")
          store(Tag("one")) must_== Set("one")
          store(Tag("nope")) must_== Set()
      }
    }
  }
}
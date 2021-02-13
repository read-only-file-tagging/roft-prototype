package roft

import org.specs2.mutable.Specification

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

    "cleanup" >> {
      Files.deleteIfExists(tempDirectory)
    }
  }
}
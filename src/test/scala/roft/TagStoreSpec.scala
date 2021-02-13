package roft

import org.specs2.mutable.Specification

import java.io.File

class TagStoreSpec extends Specification {

  "sample store 1" should {
    val store = TagStore.read(new File("src/test/resources/stores/1"))

    "have correct version" in {
      store.version must be equalTo "0.1"
    }
  }
}
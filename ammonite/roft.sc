#!/usr/bin/env amm

import ammonite.ops._

interp.repositories() ++= Seq(coursierapi.IvyRepository.of(
  {
    val h = home.toString()
    s"file://$h/.ivy2/local"
  }
 ))

import $ivy.`roft::roft-prototype:0.1`
import _root_.roft.cli.Tools



@main
def version() = {
  println("")
}

@main
def activate() = {
  println(s"WD: $pwd")
}
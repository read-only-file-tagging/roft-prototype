name := "roft-prototype"

organization := "roft"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "ammonite-ops" % "2.3.8",
  "org.specs2" %% "specs2-core" % "4.10.0" % "test",
)

scalacOptions in Test ++= Seq("-Yrangepos")
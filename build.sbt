name := """hiveudf"""
version := "0.0.1"
organization := "com.ardentex"

scalaVersion := "2.10.6"
scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")

libraryDependencies ++= Seq(
  "org.apache.hive" % "hive-exec" % "1.2.1" % "provided",
  "org.apache.hadoop" % "hadoop-core" % "1.2.1" % "provided",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Without this repo, you might get a failure trying to resolve transitive
// dependency org.pentaho:pentaho-aggdesigner-algorithm:5.1.5-jhyde
resolvers += "conjars" at "http://conjars.org/repo"

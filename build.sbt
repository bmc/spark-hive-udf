name := """spark-hive-udf"""
version := "0.1.0"
organization := "com.ardentex"

scalaVersion := "2.11.11"
scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")
crossScalaVersions := Seq(scalaVersion.value, "2.10.6")

libraryDependencies ++= Seq(
  "org.apache.hive"   % "hive-exec"   % "2.1.1" % Provided,
  "org.apache.hadoop" % "hadoop-core" % "1.2.1" % Provided,
  "org.scalatest"    %% "scalatest"   % "3.0.1" % Test
)

// Without this repo, you might get a failure trying to resolve transitive
// dependency org.pentaho:pentaho-aggdesigner-algorithm:5.1.5-jhyde
resolvers += "conjars" at "http://conjars.org/repo"

addCommandAlias("jar", ";test;package")

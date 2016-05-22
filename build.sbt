name := """xpass"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.typesafe.slick" % "slick_2.11" % "2.1.0",
  "commons-codec" % "commons-codec" % "1.6"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

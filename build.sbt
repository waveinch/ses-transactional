name := """ses-transactional"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.amazonaws" % "aws-java-sdk-ses" % "1.11.295"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
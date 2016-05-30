name := """ses-transactional"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test,
  "com.iheart" %% "play-swagger" % "0.3.1-PLAY2.5",
  "com.amazonaws" % "aws-java-sdk" % "1.10.66"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
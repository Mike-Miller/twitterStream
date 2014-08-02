name := "twitter-stream"
 
version := "0.1.0 "
 
scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-stream" % "3.0.3",
  "com.typesafe.akka" %% "akka-actor" % "2.2.1")
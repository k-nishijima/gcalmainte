import AssemblyKeys._

name := "gcalmainte"

version := "1.0"

scalaVersion := "2.9.2"

resolvers += "google-api-services" at "http://mavenrepo.google-api-java-client.googlecode.com/hg"

libraryDependencies ++= Seq(
    "joda-time" % "joda-time" % "2.1",
    "org.joda" % "joda-convert" % "1.1",
    "com.google.apis" % "google-api-services-calendar" % "v3-rev16-1.8.0-beta",
    "com.google.http-client" % "google-http-client-jackson2" % "1.12.0-beta",
    "com.google.oauth-client" % "google-oauth-client-jetty" % "1.12.0-beta"
)

scalacOptions ++= Seq("-encoding", "UTF-8")

assemblySettings

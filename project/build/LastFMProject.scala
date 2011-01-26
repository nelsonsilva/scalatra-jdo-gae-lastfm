import sbt._

class MyProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus{
  val scalatra = "org.scalatra" %% "scalatra" % "2.0.0.M2"
  val lift_json = "net.liftweb" %% "lift-json" % "2.2"
  //val servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  // Pick your favorite slf4j binding
  //val slf4jBinding = "ch.qos.logback" % "logback-classic" % "0.9.25" % "runtime"

  //val twig = "com.vercer.engine.persist" % "twig-persist" % "1.0.4"
  //val objectify= "com.googlecode.objectify" % "objectify" % "2.2.2"
  //val jpa = "org.apache.geronimo.specs" % "geronimo-jpa_3.0_spec" % "1.1.1" % "provided"

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  //val twigRepo = "Twig" at "http://mvn.twig-persist.googlecode.com/hg"
  //val objectifyRepo = "Objectofy" at "http://objectify-appengine.googlecode.com/svn/maven"
}

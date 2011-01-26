import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  lazy val appEngine = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.1" from "http://cloud.github.com/downloads/Yasushi/sbt-appengine-plugin/sbt-appengine-plugin-2.1.jar"
}

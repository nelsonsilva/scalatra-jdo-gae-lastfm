package pt.inevo.lastfm

import org.scalatra._
import net.liftweb.json._
import net.liftweb.json.Serialization.write

class LastFMFilter extends ScalatraFilter  {

  before {
    contentType = "application/json"
  }

  implicit val formats = Serialization.formats(NoTypeHints)

  def toJSON(obj:AnyRef) = write(obj)


  get("/:userId/recentTracks") {
    toJSON (  User(params("userId")).getRecentTracks())
  }
}

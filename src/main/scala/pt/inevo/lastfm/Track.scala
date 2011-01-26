package pt.inevo.lastfm
import dispatch._
import Http._
import jdo.Eq
import scala.collection.mutable.Map
import scala.xml._
import scala.collection.JavaConversions._
import pt.inevo.lastfm.storage._
import javax.jdo.annotations._

@PersistenceCapable( identityType = IdentityType.APPLICATION)
case class Track (
        var id:Int,
        var name:String,
        var artist:storage.Key,
        var url:String,
        var duration:Int,
        var listeners:Int,
        var playcount:Int,
        var userplaycount:Int,
        var userloved:Int
        ) extends Storable{


  val MinutesPerHour = 60.0
  val SecondsPerMinute = 60.0
  val MillisecondsPerSecond = 1000.0


  def timeInMinutes:Double = {
    duration / MillisecondsPerSecond / SecondsPerMinute
  }

  def timeInSeconds:Double = {
    duration / MillisecondsPerSecond
  }

  def totalListeningTimeInMinutes:Double = {
    userplaycount * timeInMinutes
  }

  def totalListeningTimeInSeconds:Double = {
    userplaycount * timeInSeconds
  }
}

object Track extends Service[Track]{
  def apply(artist:Artist, track:String="", mbid:String=""):Track = {
    // find all long hair musicians working for Sony but skip the first 4 sorted by name now!
    find(classOf[Track]).where(
      Eq("name",track),
      Eq("artist",artist.key))
            .findOne match {
      case Some(t:Track) => t
      case None =>
        LastFM.Track.getInfo(
          "artist" -> artist.name,
          "track" -> track,
          "mbid" -> mbid) match {
          case NodeSeq.Empty => null
          case info:NodeSeq=>
            val t = new Track(
              name = (info \ "name").text,
              artist = artist.key,
              id = toInt((info \ "id").text),
              url = (info \ "url").text,
              duration = toInt((info \ "duration").text),
              listeners = toInt((info \ "listeners").text),
              playcount = toInt((info \ "playcount").text),
              userplaycount = toInt((info \ "userplaycount").text),
              userloved = toInt((info \ "userloved").text)
              )
            t.update()
            t
        }

    }

  }

}
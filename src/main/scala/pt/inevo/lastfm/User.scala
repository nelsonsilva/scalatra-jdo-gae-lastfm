package pt.inevo.lastfm
import dispatch._
import Http._
import jdo.Eq
import scala.xml._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import collection.JavaConversions._
import java.util.{List=>JList}
import pt.inevo.lastfm.storage._
import javax.jdo.annotations._

case class RecentTrack(date:Long,track:Track) extends Storable

@PersistenceCapable( identityType = IdentityType.APPLICATION)
case class User(
        var id:String,
        var name:String,
        var realname:String,
        var url:String,
        var images:JList[String],
        var country:String,
        var age:Int,
        var gender:String,
        var subscriber:Int,
        var playcount:Int,
        var playlists:Int,
        var bootstrap:Int  ) extends Storable{

  @Persistent var recentTracks:JList[RecentTrack]=_
  /*
  def getTopArtists(timePeriod:String = User.OVERALL)={
    LastFM.User.getTopArtists("user" -> userId) map { (node:Node) =>
        val newArtist = new Artist()
        newArtist.setValues(node)
        newArtist
      }
      


  }  */

  def artistTracks(artistName:String, startTime:Int = -1, endTime:Int = -1, page:Int = -1)={
    LastFM.User.getArtistTracks(
      "user" -> id,
      "artist" -> artistName,
      "startTimestamp" -> startTime.toString,
      "endTimestamp"->endTime.toString,
      "page"->page.toString) map {(node) =>
      val artist = Artist(
        mbid = (node \ "artist" \ "@mbid").text,
        name = (node \ "artist").text)
      val trackNameString = (node \ "name").text
      Track(artist=artist, track=trackNameString)
    }

  }

  def getRecentTracks(limit:Int = 10, page:Int = 1) = {
    LastFM.User.getRecentTracks(
      "user" -> id,
      "limit"->limit.toString,
      "page"->page.toString) map { (node) =>
      val artist = Artist(
        mbid = (node \ "artist" \ "@mbid").text,
        name = (node \ "artist" ).text)
      val trackNameString = (node \ "name").text
      val date = (node \ "date" \ "@uts").text.toLong
              RecentTrack( date, Track(artist=artist, track=trackNameString) )
    }
  }

  /*
  def getNumberOfRecentTracksPages(): Int = {
    val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    val getRecentTracksUrl:String = LastFM.makeUrlRequest("user.getRecentTracks", paramMap)
    var responseSeq = LastFM.http(getRecentTracksUrl <> { _ \\ "recenttracks" })
    var totalPagesInt = 0
    val tempNode = responseSeq.headOption
    tempNode match {
      case None => {println("No first element in getNumberOfRecentTracks")}
      case Some(x) => {
        val totalPagesOption = x.attribute("totalPages")
        var totalPagesInt = 0
        totalPagesOption match{
          case None => {println("No total pages attribute")}
          case Some(tp) => {
            totalPagesInt = Integer.parseInt(tp.toString)
          }
        }
      }
    }
    return totalPagesInt
  }*/
}

object User extends Service[User]{
  val OVERALL = "overall"
  val SEVEN_DAYS = "7day"
  val THREE_MONTHS = "3month"
  val SIX_MONTHS = "6month"
  val TWELVE_MONTHS = "12month"


  def apply(userId:String):User = {
    find(classOf[User]).where(Eq("name",userId)).findOne match {
      case Some(u:User) => u
      case _ =>
        LastFM.User.getInfo("user" -> userId) match {
          case NodeSeq.Empty => null
          case info:NodeSeq =>
            val u = new User(
              id = (info \ "id").text,
              name = (info \ "name").text,
              realname = (info \ "realname").text,
              url = (info \ "url").text,
              images = (info \ "image").map(_.text),
              country = (info \ "country").text,
              age = toInt( (info \ "age").text),
              gender = (info \ "gender").text,
              subscriber = toInt( (info \ "subscriber").text),
              playcount = toInt( (info \ "playcount").text),
              playlists = toInt( (info \ "playlists").text),
              bootstrap = toInt( (info \ "bootstrap").text))

            u.update()
            u
        }
    }
  }

}

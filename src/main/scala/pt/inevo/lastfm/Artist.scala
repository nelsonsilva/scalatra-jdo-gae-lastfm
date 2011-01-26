package pt.inevo.lastfm

import jdo.{FilterCriterion, Eq}
import scala.xml._
import javax.jdo.annotations._
import pt.inevo.lastfm.storage.{Service, Storable}
import collection.JavaConversions._
import java.util.{List=>JList}

@PersistenceCapable( identityType = IdentityType.APPLICATION)
case class Artist(var name:String, var mbid:String, var url:String) extends Storable {
}

object Artist extends Service[Artist]{
  def apply(artistNode:NodeSeq)= new Artist(
    name = (artistNode \ "name").text,
    mbid = (artistNode \ "mbid").text,
    url = (artistNode \ "url").text )


  def apply(mbid:String="",name:String=""):Artist = {
    // find all long hair musicians working for Sony but skip the first 4 sorted by name now!
    var c:List[FilterCriterion]=Nil
    if(mbid.nonEmpty)
      c::=Eq("mbid",mbid)
    if(name.nonEmpty)
      c::=Eq("name",name)
    find(classOf[Artist]).where( c:_*)
            .findOne match {
      case Some(a:Artist) => println(a); a
      case None =>
        LastFM.Artist.getInfo(
          "artist" -> name,
          "mbid" -> mbid) match {
          case NodeSeq.Empty => null
          case info:NodeSeq=>
            val a = Artist(info)
            println(a)
            a.update()
            a
        }

    }

  }

}
package pt.inevo.lastfm

import dispatch._
import scala.collection.mutable.Map
import collection.SortedMap
import xml.NodeSeq

object LastFM{
  lazy val endpoint = :/("ws.audioscrobbler.com") / "2.0"

  //val http = new AppEngineHttp

  def method(methodName:String)(params:(String,Any)*) =  Map("api_key" -> AccountInfo.apiKey, "method" -> methodName) ++ params

  def get[T](methodName:String)(params: (String,Any)*)(block: Seq[xml.Node] => NodeSeq): NodeSeq =
    AppEngineHttp(endpoint <<?   method(methodName)(params:_*) <> {rsp => handleResponse(rsp)(block)})

  def handleResponse(rsp: xml.Elem)(block: Seq[xml.Node] => NodeSeq): NodeSeq = rsp match {
    case rsp if (rsp \ "@status").text == "ok" =>
      block(rsp.nonEmptyChildren)
    case rsp if (rsp \ "@stat").text == "failed" =>
      NodeSeq.Empty  // ERROR!
  }

  object Album {}

  object Track {
    def getInfo(params: (String, Any)*) = get("track.getInfo")(params:_*){ _ \\ "track" }
  }

  object User {

    def getInfo(params: (String, Any)*) = get("user.getInfo")(params:_*){ _ \\ "user" }

    def getTopArtists(params: (String, Any)*) = get("user.getTopArtists")(params:_*){ _ \\ "artist" }


    def getRecentTracks(params: (String, Any)*) = get("user.getRecentTracks")(params:_*){ _ \\ "track" }


    def getArtistTracks(params: (String, Any)*) = get("user.getRecentTracks")(params:_*){ _ \\ "track" }

  }

  object Artist {
    def getInfo(params: (String, Any)*) = get("artist.getInfo")(params:_*){ nodes:Seq[xml.Node] => nodes }
  }
}
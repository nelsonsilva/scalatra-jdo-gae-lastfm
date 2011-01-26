package pt.inevo.lastfm

import javax.jdo.{PersistenceManager, JDOHelper}
import jdo.ScalaQuery
import javax.jdo.annotations._

object storage {

  type Key = java.lang.Long //com.google.appengine.api.datastore.Key

  val PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");

  def getPM = PMF.getPersistenceManager()

  def withDB[T](block: PersistenceManager=> T ) = {
    val pm = getPM
    try {
      block(pm)
    } finally {
      pm.close();
    }
  }

  trait Storable{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    var key : Key = _

    def update()=withDB{ pm=> pm.makePersistent(this); println(this)}

  }



  trait Storage[T<:Storable] {

    //def load(k:AnyRef)(implicit m: scala.reflect.Manifest[T])=Option(DataStore.load(m.getClass,k))
    def find(clazz:Class[T])=new ScalaQuery[T](getPM,clazz)

    def load(k:AnyRef)=Option(withDB{ _.getObjectById(k).asInstanceOf[T]})
  }

  trait Service[T<:Storable] extends Storage[T]{
    def toInt(s:String)={
      try {
        s.toInt
      } catch {
        case _ => 0
      }
    }

  }
}
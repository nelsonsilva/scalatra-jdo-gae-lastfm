package pt.inevo.lastfm.jdo

import pt.inevo.lastfm.storage.Storable
import javax.jdo.PersistenceManager
import collection.mutable.Queue


class ScalaQuery[T<:Storable](pm:PersistenceManager,clazz:Class[T]) {
  private val filterCriteria = new Queue[FilterCriterion]

  private def parameters = filterCriteria.map(_.parameter).toArray
  def filter = {
    filterCriteria.toList.zipWithIndex.map{
      case (f, i) => f.queryString(":"+i)
    }.mkString(" && ")
  }

  def where(criteria:FilterCriterion *)  = {
    filterCriteria ++= criteria
    this
  }

  def findOne():Option[T] = Option(getSingleResult)

  def getSingleResult():T = {
    val query = newQuery
    query.setUnique(true)
    query.executeWithArray(parameters:_*).asInstanceOf[T]
  }

  def newQuery = {
    val query = pm.newQuery(clazz)
    if(!filterCriteria.isEmpty) {
      query.setFilter(filter)
    }
    query
  }
}

trait Criterion{
  val property:String
}

abstract class FilterCriterion(val pattern:String) extends Criterion {
  val parameter:AnyRef
  def queryString(parameterName:String):String =
    pattern.format(property, parameterName)
}

case class Eq(override val property:String, override val parameter:AnyRef)
        extends FilterCriterion("%s == %s")
package kang.net.controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives
import kang.net.persistence.EventDao
import kang.net.utils.JsonMapping
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class EventController(eventDao: EventDao)
  extends Directives
  with JsonMapping {

  private val eventPathPrefix = pathPrefix(
    PathParts.Api / PathParts.Events)

  val listCalendars: Route =
    (eventPathPrefix & get)(getMonthEvents)

  private def getMonthEvents: Route = {
    val res = for {
      events <- eventDao.getEventsInTheMonth()
    } yield events

    // TODO: perhaps to sync db driver
    onSuccess(res) { events =>
      complete(events.toJson.prettyPrint)
    }
  }

  val route: Route = getMonthEvents
}

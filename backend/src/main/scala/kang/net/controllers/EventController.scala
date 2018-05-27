package kang.net.controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives
import kang.net.model.Event
import kang.net.persistence.EventDao
import kang.net.utils.JsonMapping
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

class EventController(eventDao: EventDao) extends Directives with JsonMapping {

  private val eventPathPrefix = pathPrefix(PathParts.Api / PathParts.Events)

  val listEvents: Route =
    (eventPathPrefix & get)(getMonthEvents)

  val insertEvent: Route =
    (eventPathPrefix & post)(upsertEvent)

  val getEvent: Route =
    (eventPathPrefix & get)(getEventById)

  private def getEventById: Route = path(IntNumber) { id =>
    val res = for {
      event <- eventDao.getEventById(id)
    } yield event

    onSuccess(res) { event =>
      complete(event)
    }

  }

  private def upsertEvent: Route = entity(as[Event]) { event =>
    for {
      _ <- eventDao.insertEvent(event)
    } yield ()

    complete("200")
  }

  private def getMonthEvents: Route = {
    val res = for {
      events <- eventDao.getEventsInTheMonth()
    } yield events

    // TODO: perhaps to sync db driver
    onSuccess(res) { events =>
      complete(events.toJson.prettyPrint)
    }
  }

  val route: Route = getEvent ~ listEvents ~ insertEvent
}

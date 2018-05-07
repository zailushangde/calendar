package kang.net.controllers

import akka.http.scaladsl.server.{Directives, Route}
import kang.net.services.CalendarService
import kang.net.utils.JsonMapping
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import kang.net.model.Today

class CalendarController(calendarService: CalendarService)
    extends Directives
    with JsonMapping {

  private val calendarsPathPrefix = pathPrefix(
    PathParts.Api / PathParts.Calendars)

  val getCalendars: Route =
    (calendarsPathPrefix & get)(getTodayCalendar)

  private def getTodayCalendar: Route = {
    val html = calendarService.generateCalendarHtml(Today.apply)
    complete(
      HttpResponse(
        entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, html.trim)))
  }

  val route: Route = getCalendars
}

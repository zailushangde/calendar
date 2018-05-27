package kang.net.controllers

import akka.http.scaladsl.server.{Directives, Route}
import kang.net.utils.JsonMapping
import kang.net.model.{MyCalendar, Today}
import spray.json._

class CalendarController()
    extends Directives
    with JsonMapping {

  private val calendarsPathPrefix = pathPrefix(
    PathParts.Api / PathParts.Calendars)

  val getCalendars: Route =
    (calendarsPathPrefix & get)(getTodayCalendar)

  private def getTodayCalendar: Route = {
    val today = Today.apply
    val calendar = MyCalendar(today, today.getFirstDayOfWeek, today.getDaysInTheMonth)
    complete(calendar.toJson.prettyPrint)
  }

  val route: Route = getCalendars
}

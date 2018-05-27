package kang.net

import cats.data.EitherT
import cats.instances.future._
import io.circe
import io.circe.parser.decode
import kang.net.model.{Event, MyCalendar, Today}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._

import scala.concurrent.Future

object Bootstrap {

  // temp import
  import TestBackendData._

  private def getDateFromBacked: EitherT[Future, circe.Error, (MyCalendar, List[Event])] = {
    for {
      calendarRes <- EitherT.right(calendarResFromBackend)
      eventsRes   <- EitherT.right(eventsResFromBackend)
      calendar    <- EitherT.fromEither(decode[MyCalendar](calendarRes))
      events      <- EitherT.fromEither(decode[List[Event]](eventsRes))
    } yield (calendar, events)
  }

  @JSExportTopLevel("generateMonthValue")
  def generateMonthValue(month: html.Span): Future[Either[circe.Error, Unit]] = {
    getDateFromBacked.value.map(_.map {
      case (calendar, _) =>
        val today: Today = calendar.today
        month.textContent = today.month + " 月"
    })
  }

  @JSExportTopLevel("generateDaysWithEvent")
  def generateDaysWithEvent(days: html.UList): Future[Either[circe.Error, Unit]] = {
    getDateFromBacked.value.map(_.map {
      case (calendar, events) =>
        val today: Today = calendar.today
        val eventsByDate = events.groupBy(_.eventStart.getDayOfMonth)

        for (day <- 1 until (today.getDaysInTheMonth + calendar.firstDayOfWeek)) {
          val res =
            if (day < calendar.firstDayOfWeek)
              li
            else if (day == (today.date + calendar.firstDayOfWeek - 1))
              li(span(`class` := "active")(today.date))
            else
              li(day - (calendar.firstDayOfWeek - 1))

          val eventRes = eventsByDate.get(day - (calendar.firstDayOfWeek - 1)).map { list =>
            res(a(href := "TODO")(list.head.title))
          }

          days.appendChild(
            eventRes.fold(res)(r => r).render
          )
        }
    })
  }
}

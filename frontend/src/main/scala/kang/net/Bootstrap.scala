package kang.net

import java.time.Month
import java.time.format.{DateTimeFormatter, TextStyle}
import java.util.Locale

import cats.data.EitherT
import cats.instances.future._
import io.circe
import io.circe.parser.decode
import kang.net.model.{Event, MyCalendar, Today}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import org.scalajs.dom.raw.Node

import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._

import scala.concurrent.Future

object Bootstrap {

  // temp import
  import kang.net.temp.TestBackendData._

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
        month.textContent = Month.of(today.month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
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
            res(a(href := s"event_view.html?event_id=${list.head.id}")(list.head.title))
          }

          days.appendChild(
            eventRes.fold(res)(r => r).render
          )
        }
    })
  }

  @JSExportTopLevel("displayEvent")
  def displayEvent(eventId: Int, eventView: html.Span): EitherT[Future, circe.Error, Node] = {
    val url = s"http://localhost:9001/api/events/$eventId"

    for {
      response <- EitherT.right(Ajax.get(url))
      event    <- EitherT.fromEither(decode[Event](response.responseText))
    } yield {
      val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

      val monthName   = event.eventStart.getMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
      val dateValue   = event.eventEnd.getDayOfMonth
      val yearValue   = event.eventStart.getYear
      val eventStart  = event.eventStart.format(timeFormatter)
      val eventEnd    = event.eventEnd.format(timeFormatter)
      val displayDate = s"$monthName $dateValue, $yearValue, $eventStart - $eventEnd"
      println(displayDate + " yyy")
      eventView.appendChild(h2(event.title).render).render
      eventView.appendChild(p(`class` := "dates")(displayDate).render)
      eventView.appendChild(p(`class` := "desc")(event.description).render)
    }

  }
}

package kang.net

import java.time.{LocalDateTime, Month}
import java.time.format.{DateTimeFormatter, TextStyle}
import java.util.Locale

import cats.data.EitherT
import cats.instances.future._
import io.circe
import io.circe.parser.decode
import io.circe.syntax._
import kang.net.model.{Event, MyCalendar, Today}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html
import org.scalajs.dom.raw.Node
import org.scalajs.dom.ext.Ajax._

import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._

import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}

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

  @JSExportTopLevel("displayForm")
  def displayForm(fieldSet: html.FieldSet, eventId: js.UndefOr[Int]) = {
    var submit = "Create a New Event"

    val maybeEvent: Option[EitherT[Future, circe.Error, Option[Event]]] = eventId.toOption.map { id =>
      val url = s"http://localhost:9001/api/events/$id"
      submit = "Edit The Event"
      for {
        response <- EitherT.right(Ajax.get(url))
        event    <- EitherT.fromEither(decode[Option[Event]](response.responseText))
      } yield event
    }

    maybeEvent.getOrElse(EitherT.fromEither(Right(None))).value.map {
      _.map { event: Option[Event] =>
        println(event)
        val maybeTitle      = event.map(_.title).getOrElse("")
        val maybeEventStart = event.map(_.eventStart.toString()).getOrElse("")
        val maybeEventEnd   = event.map(_.eventEnd.toString()).getOrElse("")
        val maybeDesc       = event.map(_.description).getOrElse("")

        eventId.toOption.map(i => fieldSet.appendChild(p(id := "event_id", hidden)(i).render))

        fieldSet.appendChild(label(`for` := "event_title")("Event Title").render)
        fieldSet.appendChild(input(`type` := "text", name := "event_title", id := "event_title", value := maybeTitle).render)

        fieldSet.appendChild(label(`for` := "event_start")("Start Time").render)
        fieldSet.appendChild(input(`type` := "text", name := "event_start", id := "event_start", value := maybeEventStart).render)

        fieldSet.appendChild(label(`for` := "event_end")("End Time").render)
        fieldSet.appendChild(input(`type` := "text", name := "event_end", id := "event_end", value := maybeEventEnd).render)

        fieldSet.appendChild(label(`for` := "event_desc")("Event Description").render)
        fieldSet.appendChild(textarea(`type` := "text", name := "event_desc", id := "event_desc")(maybeDesc).render)

        fieldSet.appendChild(input(`type` := "submit", id := "submit", name := "name", value := submit).render)

        fieldSet.appendChild(a(href := "./")("cancel").render)

        dom.document.getElementById("submit").addEventListener("click", postEvent _)
      }
    }
  }

  def postEvent(mouseEvent: dom.MouseEvent) = {
    val url         = "http://localhost:9001/api/events"
    val event_id    = Option(dom.document.getElementById("event_id")).map(_.asInstanceOf[html.Paragraph].textContent)
    val title       = dom.document.getElementById("event_title").asInstanceOf[html.Input].value
    val event_start = dom.document.getElementById("event_start").asInstanceOf[html.Input].value
    val event_end   = dom.document.getElementById("event_end").asInstanceOf[html.Input].value
    val desc        = dom.document.getElementById("event_desc").asInstanceOf[html.Input].value
    println("the button is triggered " + event_id)

    val postBody = Event(event_id.map(_.toInt), title, desc, LocalDateTime.parse(event_start), LocalDateTime.parse(event_end))
    println(postBody.asJson.noSpaces)
    Ajax
      .post(url, InputData.str2ajax(postBody.asJson.noSpaces), headers = Map("Content-Type" -> "application/json"))
      .onComplete {
        case Success(_) => println("success")
        case Failure(_) => println("failure")
      }
  }
}

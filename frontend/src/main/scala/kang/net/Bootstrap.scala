package kang.net

import java.net.URLDecoder
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
import org.scalajs.dom.ext.Ajax._

import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._

import scala.concurrent.Future
import scala.scalajs.js
import scala.util.{Failure, Success}

object Bootstrap {
  private val baseUrl = s"http://localhost:9001/api/"

  private def getDateFromBacked: EitherT[Future, circe.Error, (MyCalendar, List[Event])] = {
    for {
      calendarRes <- EitherT.right(Ajax.get(baseUrl + "calendars"))
      eventsRes   <- EitherT.right(Ajax.get(baseUrl + "events"))
      calendar    <- EitherT.fromEither(decode[MyCalendar](calendarRes.responseText))
      events      <- EitherT.fromEither(decode[List[Event]](eventsRes.responseText))
    } yield (calendar, events)
  }

  private def getDateForWeekView(rawCalendar: String) = {
    for {
      eventsRes <- EitherT.right(Ajax.get(baseUrl + "events"))
      calendar  <- EitherT.fromEither(decode[MyCalendar](rawCalendar))
      events    <- EitherT.fromEither(decode[List[Event]](eventsRes.responseText))
    } yield (calendar, events)
  }

  @JSExportTopLevel("generateDaysWithEvent")
  def generateDaysWithEvent(days: html.UList): Future[Either[circe.Error, Unit]] = {
    getDateFromBacked.value.map(_.map {
      case (calendar, events) =>
        val today: Today   = calendar.today
        val eventsByDate   = events.groupBy(_.eventStart.getDayOfMonth)
        val firstDayOfWeek = calendar.firstDayOfWeek

        // generate month value
        dom.document.getElementById("month").asInstanceOf[html.Span].textContent =
          Month.of(today.month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        // generate days list
        for (day <- 1 until (today.getDaysInTheMonth + firstDayOfWeek)) {
          val res =
            if (day < firstDayOfWeek)
              li
            else if (day == (today.date + firstDayOfWeek - 1))
              li(value := today.date, span(`class` := "active")(today.date))
            else
              li(value := day - (firstDayOfWeek - 1))(day - (firstDayOfWeek - 1))

          val eventRes = eventsByDate.get(day - (firstDayOfWeek - 1)).map { eventList =>
            res(li(List.fill(eventList.size)("。")))
          }

          days.appendChild(
            eventRes.fold(res)(r => r).render
          )
        }

        val listLi = dom.document
          .getElementsByClassName("days")
          .item(0)
          .getElementsByTagName("li")

        for (elm <- 0 to listLi.length) {
          listLi
            .item(elm)
            .addEventListener(
              "click",
              (mouseEvent: dom.MouseEvent) => {
                val dateForWeekView      = mouseEvent.target.asInstanceOf[html.LI].value
                val dayOfWeekForWeekView = (dateForWeekView - today.date + today.dayOfWeek + 7) % 7
                println(dateForWeekView)
                val newCalendar =
                  calendar.copy(today =
                    today.copy(date = dateForWeekView, dayOfWeek = if (dayOfWeekForWeekView == 0) 7 else dayOfWeekForWeekView))
                //              dom.document.location.href = "/display_event.html"
                dom.document.location.href = s"/week_view.html?calendar=${newCalendar.asJson}"
              }
            )
        }
    })
  }

  @JSExportTopLevel("displayEvent")
  def displayEvent(eventId: Int, eventView: html.Span): EitherT[Future, circe.Error, Unit] = {
    val url = s"${baseUrl}events/$eventId"

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

      eventView.appendChild(h2(event.title).render).render
      eventView.appendChild(p(`class` := "dates")(displayDate).render)
      eventView.appendChild(p(`class` := "desc")(event.description).render)

      val backButton = button()("Edit Event").render
      eventView.appendChild(backButton)
      eventView.appendChild(button(`type` := "delete", id := "delete")("Delete").render)

      backButton.onclick = (_: dom.MouseEvent) => {
        dom.document.location.href = s"/display_event.html?event_id=$eventId"
      }

      dom.document
        .getElementById("delete")
        .addEventListener("click", (_: dom.MouseEvent) => {
          if (dom.window.confirm("Are you sure to delete?"))
            handleComplete(Ajax.delete(url))
        })
    }
  }

  @JSExportTopLevel("displayForm")
  def displayForm(fieldSet: html.FieldSet, eventId: js.UndefOr[Int]): Future[Either[circe.Error, Unit]] = {
    var submit = "Create a New Event"

    val maybeEvent: Option[EitherT[Future, circe.Error, Option[Event]]] = eventId.toOption.map { id =>
      val url = s"${baseUrl}events/$id"
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
        fieldSet.appendChild(
          input(`type` := "datetime-local", name := "event_start", id := "event_start", value := maybeEventStart).render)

        fieldSet.appendChild(label(`for` := "event_end")("End Time").render)
        fieldSet.appendChild(
          input(`type` := "datetime-local", name := "event_end", id := "event_end", value := maybeEventEnd).render)

        fieldSet.appendChild(label(`for` := "event_desc")("Event Description").render)
        fieldSet.appendChild(textarea(`type` := "text", name := "event_desc", id := "event_desc")(maybeDesc).render)

        fieldSet.appendChild(input(`type` := "submit", id := "submit", name := "name", value := submit).render)

        fieldSet.appendChild(a(href := "./")("cancel").render)

        dom.document.getElementById("submit").addEventListener("click", postEvent _)
      }
    }
  }

  def postEvent(mouseEvent: dom.MouseEvent): Unit = {
    val url         = s"${baseUrl}events"
    val event_id    = Option(dom.document.getElementById("event_id")).map(_.asInstanceOf[html.Paragraph].textContent)
    val title       = dom.document.getElementById("event_title").asInstanceOf[html.Input].value
    val event_start = dom.document.getElementById("event_start").asInstanceOf[html.Input].value
    val event_end   = dom.document.getElementById("event_end").asInstanceOf[html.Input].value
    val desc        = dom.document.getElementById("event_desc").asInstanceOf[html.Input].value

    val postBody = Event(event_id.map(_.toInt), title, desc, LocalDateTime.parse(event_start), LocalDateTime.parse(event_end))
    println(postBody.asJson.noSpaces)
    handleComplete(
      Ajax
        .post(
          url,
          InputData.str2ajax(postBody.asJson.noSpaces),
          headers = Map("Content-Type" -> "application/json")
        ))
  }

  def handleComplete(res: Future[dom.XMLHttpRequest]): Unit = {
    res.onComplete {
      case Success(_) =>
        dom.document.location.href = "/"
      case Failure(_) =>
        dom.window.alert("Something Wrong, please try again")
    }
  }

  @JSExportTopLevel("displayWeekView")
  def displayWeekView(rawCalendar: String) = {

    val decodeCalenderFromUrl = URLDecoder.decode(rawCalendar)
    println(decodeCalenderFromUrl)

    getDateForWeekView(decodeCalenderFromUrl).map {
      case (calendar, events) =>
        val eventsByDate = events.groupBy(_.eventStart.getDayOfMonth)
        val eventView    = dom.document.getElementById("events")
        val weekDaysView = dom.document.getElementById("weekdays")

        val date      = calendar.today.date
        val dayOfDate = calendar.today.dayOfWeek

        for (index <- (date - (dayOfDate - 1)) to (date + (7 - dayOfDate))) {
          println(index - (date - (dayOfDate - 1)))

          if (index > 0)
            weekDaysView
              .getElementsByTagName("li")
              .item(index - (date - (dayOfDate - 1)))
              .appendChild(span(index).render)

          eventsByDate.get(index).map { list =>
            println(list.head)
            val event      = list.head
            val eventEnd   = event.eventEnd
            val eventStart = event.eventStart
            val base       = 112.84

            val height = eventEnd.getHour * 60 + eventEnd.getMinute - eventStart.getMinute - eventStart.getHour * 60
            val top    = eventStart.getHour * 60 + eventStart.getMinute - 9 * 60
            val width  = base
            val left   = base * (eventStart.getDayOfWeek.getValue % 7)

            val node =
              div(`class` := "event", style := s"top: ${top}px; left: ${left}px; height: ${height}px; width: ${width}px").render
            node.innerHTML =
              s"<span class='title'> ${event.title} </span> <br> <span class='title'> ${eventStart.getHour}:${eventStart.getMinute} - ${eventEnd.getHour}:${eventEnd.getMinute} </span>"

            node.addEventListener(
              "click",
              (_: dom.MouseEvent) => {
                dom.document.location.href = s"event_view.html?event_id=${list.head.id.get}"
              }
            )

            eventView.appendChild(node)
          }
        }

        val listLi = dom.document
          .getElementsByClassName("days")
          .item(0)
          .getElementsByTagName("li")

        for (elm <- 0 to listLi.length) {
          listLi
            .item(elm)
            .addEventListener(
              "click",
              (_: dom.MouseEvent) => {
                dom.document.location.href = "/display_event.html"
              }
            )
        }
    }
    val divCollection = dom.document.getElementById("grid").getElementsByTagName("div")
    for (index <- 0 to divCollection.length) {
      divCollection
        .item(index)
        .addEventListener("click", (_: dom.MouseEvent) => {
          dom.document.location.href = s"/display_event.html"
        })
    }
  }
}

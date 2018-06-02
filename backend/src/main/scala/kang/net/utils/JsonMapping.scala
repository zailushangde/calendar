package kang.net.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import kang.net.model.{Event, MyCalendar, Today}
import org.joda.time.{LocalDateTime => JodaLocalDateTime}
import spray.json._

import scala.util.{Failure, Success, Try}

trait JsonMapping extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val todayFormat: RootJsonFormat[Today] = jsonFormat(Today.apply, "year", "month", "date", "day_of_week")
  implicit val calendarFormat: RootJsonFormat[MyCalendar] = jsonFormat(MyCalendar.apply, "today", "first_day_of_week", "days")

  implicit val jodaLocalDateTimeFormat: JsonFormat[JodaLocalDateTime] =
    new JsonFormat[JodaLocalDateTime] {
      override def write(obj: JodaLocalDateTime): JsValue = JsString(obj.toString)

      override def read(json: JsValue): JodaLocalDateTime = json match {
        case JsString(s) => Try(JodaLocalDateTime.parse(s)) match {
          case Success(result) => result
          case Failure(exception) =>
            deserializationError(s"could not parse $s as Joda LocalDateTime", exception)
        }
        case notAJsString =>
          deserializationError(s"expected a String but got a $notAJsString")
      }
    }

  implicit val eventFormat: RootJsonFormat[Event] = jsonFormat(Event.apply, "id", "title", "description", "event_start", "event_end", "available")
}

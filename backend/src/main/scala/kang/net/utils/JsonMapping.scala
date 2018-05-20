package kang.net.utils

import kang.net.model.{MyCalendar, Today}
import spray.json._

trait JsonMapping extends DefaultJsonProtocol {
  implicit val todayFormat: RootJsonFormat[Today] = jsonFormat(Today.apply, "year", "month", "date", "day_of_week")
  implicit val calendarFormat: RootJsonFormat[MyCalendar] = jsonFormat(MyCalendar.apply, "today", "first_day_of_week", "days")
}

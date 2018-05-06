package kang.net.utils

import kang.net.model.{MyCalendar, Today}
import spray.json._

trait JsonMapping extends DefaultJsonProtocol {
  implicit val todayFormat = jsonFormat(Today, "year", "month", "day")
  implicit val myCalenderFormat = jsonFormat(MyCalendar, "today", "days_of_month", "day_of_week")
}

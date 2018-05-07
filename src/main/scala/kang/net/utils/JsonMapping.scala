package kang.net.utils

import kang.net.model.Today
import spray.json._

trait JsonMapping extends DefaultJsonProtocol {
  implicit val todayFormat: RootJsonFormat[Today] = jsonFormat(Today.apply, "year", "month", "date", "day_of_week")
}

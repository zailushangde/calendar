package kang.net.model

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, ObjectEncoder}

case class MyCalendar(today: Today, firstDayOfWeek: Int, days: Int)

object MyCalendar {

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val todayEncoder: ObjectEncoder[MyCalendar] = deriveEncoder
  implicit val todayDecoder: Decoder[MyCalendar] = deriveDecoder
}
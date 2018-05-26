package kang.net.model

import java.time.ZonedDateTime

case class Event(id: Int, title: String, description: String, eventStart: ZonedDateTime, eventEnd: ZonedDateTime)

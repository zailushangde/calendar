package kang.net.model

import org.joda.time.LocalDateTime

case class Event(id: Option[Int],
                 title: String,
                 description: String,
                 eventStart: LocalDateTime,
                 eventEnd: LocalDateTime,
                 available: Boolean = true) {}

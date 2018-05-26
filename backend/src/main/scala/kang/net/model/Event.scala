package kang.net.model

import org.joda.time.LocalDateTime

case class Event(id: Int,
                 title: String,
                 description: String,
                 eventStart: LocalDateTime,
                 eventEnd: LocalDateTime) {


}

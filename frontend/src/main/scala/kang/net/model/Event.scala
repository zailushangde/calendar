package kang.net.model

import java.time.LocalDateTime
import io.circe.{Decoder, Encoder, ObjectEncoder}
import io.circe.generic.extras.Configuration
import cats.syntax.either._
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}

case class Event(id: Option[Int], title: String, description: String, eventStart: LocalDateTime, eventEnd: LocalDateTime)

object Event {
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val eventEncoder: ObjectEncoder[Event] = deriveEncoder
  implicit val eventDecoder: Decoder[Event]       = deriveDecoder

  implicit val timeEncoder: Encoder[LocalDateTime] = Encoder.encodeString.contramap[LocalDateTime](_.toString)
  implicit val timeDecoder: Decoder[LocalDateTime] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(LocalDateTime.parse(str)).leftMap(_ => "LocalDateTime")
  }
}

package kang.net.model

import java.time.LocalDate
import io.circe.{Decoder, Encoder, ObjectEncoder}
import io.circe.generic.extras.Configuration
import cats.syntax.either._
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}

case class Event(id: Int, title: String, description: String, eventStart: LocalDate, eventEnd: LocalDate)

object Event {
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val eventEncoder: ObjectEncoder[Event] = deriveEncoder
  implicit val eventDecoder: Decoder[Event] = deriveDecoder

  implicit val timeEncoder: Encoder[LocalDate] = Encoder.encodeString.contramap[LocalDate](_.toString)
  implicit val timeDecoder: Decoder[LocalDate] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(LocalDate.parse(str)).leftMap(t => "LocalDate")
  }
}
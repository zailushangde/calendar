package kang.net.temp

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TestBackendData {
  val calendarResFromBackend: Future[String] = Future("""
      |{
      |  "today": {
      |    "year": 2018,
      |    "month": 5,
      |    "date": 25,
      |    "day_of_week": 6
      |  },
      |  "first_day_of_week": 3,
      |  "days": 31
      |}
    """.stripMargin)

  val eventsResFromBackend = Future(
    """
      |[{
      |  "event_end": "2018-05-01T23:59:59.000",
      |  "description": "Enjoy Sunshine!",
      |  "event_start": "2018-05-01T00:00:00.000",
      |  "id": 1,
      |  "title": "Bank Holiday"
      |}, {
      |  "event_end": "2018-05-31T23:59:59.000",
      |  "description": "Happy End!",
      |  "event_start": "2018-05-31T00:00:00.000",
      |  "id": 2,
      |  "title": "Last Day of May"
      |}]
    """.stripMargin
  )
}

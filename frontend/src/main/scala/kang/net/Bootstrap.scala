package kang.net

import io.circe.parser.decode
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.html

import scala.concurrent.Future
import scala.scalajs.js.annotation.JSExportTopLevel
import scalatags.JsDom.all._

object Bootstrap {

  @JSExportTopLevel("generate")
  def generate(month: html.Span, days: html.UList) = {

    val res = Future(
      """
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

    res.map { r =>
      val maybeCalendar = decode[MyCalendar](r)

      maybeCalendar.map { myCalendar =>
        val today = myCalendar.today
        month.textContent = today.month + " 月"
        for (day <- 1 until (today.getDaysInTheMonth + myCalendar.firstDayOfWeek)) {
          val res =
            if (day < myCalendar.firstDayOfWeek)
              li
            else if (day == (today.date + myCalendar.firstDayOfWeek - 1))
              li(span(`class`:="active")(today.date))
            else
              li(day - (myCalendar.firstDayOfWeek - 1))

          days.appendChild(
            res.render
          )
        }
      }
    }
  }
}

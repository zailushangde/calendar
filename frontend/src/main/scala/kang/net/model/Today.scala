package kang.net.model

import java.text.SimpleDateFormat
import java.util.Calendar

import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, ObjectEncoder}

object Today {
  def apply: Today = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val calendar = Calendar.getInstance()
    val time = dateFormat.format(calendar.getTime)

    time.split("-").toList match {
      case year :: month :: day :: Nil =>
        // Month value is 0-based. e.g., 0 for January.
        calendar.set(year.toInt, month.toInt - 1, day.toInt)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        Today(year.toInt, month.toInt, day.toInt, dayOfWeek)
    }
  }

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults

  implicit val todayEncoder: ObjectEncoder[Today] = deriveEncoder
  implicit val todayDecoder: Decoder[Today] = deriveDecoder
}

/**
  * @param year      the year of the date
  * @param month     the month of the date
  * @param date      this date
  * @param dayOfWeek the day of week about the certain @date,
  *                  which starts from 1. e.g., 1 for Sunday and 5 for Friday
  */
case class Today(year: Int, month: Int, date: Int, dayOfWeek: Int) {
  private val month31 = List(1, 3, 5, 7, 8, 10, 12)

  private def isLeapYear: Boolean =
    if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0))
      true
    else
      false

  def getDaysInTheMonth: Int =
    if (month31.contains(month))
      31
    else if (month == 2) {
      if (isLeapYear)
        29
      else
        28
    } else 30

  /**
    * to get the day of week about the first day in the month
    * @return the day of week about the first day in the month
    */
  def getFirstDayOfWeek: Int =
    ((dayOfWeek - date % 7) + 7) % 7 + 1
}

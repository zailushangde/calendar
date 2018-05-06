package kang.net.services

import kang.net.model.{MyCalendar, Today}
import org.scalatest.{FlatSpec, Matchers}

class CalendarServiceSpec extends FlatSpec with Matchers {

  private val calendarService = new CalendarServiceImpl

  "The day of week for the first day in a month" should "be correct" in {

    /**
      *   date     |   dayOfWeek   |   dayOfWeek in the month
      * -----------------------------------------------------
      * 2017-03-15 |     4(We)     |       4(We)
      * 2017-09-19 |     3(Tu)     |       6(Fr)
      * 2018-01-08 |     2(Mo)     |       2(Mo)
      * 2018-02-24 |     7(Sa)     |       5(Th)
      * 2018-03-01 |     5(Th)     |       5(Th)
      */
    val testCalendarAndFirstDayOfWeek = List(
      MyCalendar(Today(2017, 3, 15), 31, 4) -> 4,
      MyCalendar(Today(2017, 9, 19), 30, 3) -> 6,
      MyCalendar(Today(2017, 1, 8), 31, 2) -> 2,
      MyCalendar(Today(2017, 2, 24), 28, 7) -> 5,
      MyCalendar(Today(2017, 3, 1), 31, 5) -> 5
    )

    testCalendarAndFirstDayOfWeek.foreach {
      case (c, target) =>
        calendarService.getFirstDayOfWeek(c.dayOfWeek, c.today.date) should ===(
          target)
    }
  }
}

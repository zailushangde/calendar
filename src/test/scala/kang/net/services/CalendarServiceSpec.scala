package kang.net.services

import kang.net.model.Today
import org.scalatest.{FlatSpec, Matchers}

class CalendarServiceSpec extends FlatSpec with Matchers {

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
    val testDateList = List(
      Today(2017, 3, 15, 4) -> 4,
      Today(2017, 9, 19, 3) -> 6,
      Today(2018, 1, 8, 2) -> 2,
      Today(2018, 2, 24, 7) -> 5,
      Today(2018, 3, 1, 5) -> 5
    )

    testDateList.foreach {
      case (oneDay, target) =>
        oneDay.getFirstDayOfWeek should ===(target)
    }
  }
}

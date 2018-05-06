package kang.net.model

case class Today(year: Int, month: Int, date: Int) {
  private val month31 = List(1, 3, 5, 7, 8, 10, 12)

  private def isLeapYear: Boolean =
    if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0))
      true
    else
      false

  def monthDays: Int =
    if (month31.contains(month))
      31
    else if (month == 2) {
      if (isLeapYear)
        29
      else
        28
    } else 30
}

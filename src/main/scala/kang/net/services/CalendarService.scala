package kang.net.services

import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.common.annotations.VisibleForTesting
import kang.net.model.{MyCalendar, Today}

trait CalendarService {
  def getTodayCalendar: MyCalendar
  def generateHtml(myCalendar: MyCalendar): String
}

class CalendarServiceImpl extends CalendarService {
  override def getTodayCalendar: MyCalendar = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val calendar = Calendar.getInstance()
    val time = dateFormat.format(calendar.getTime)

    val today = time.split("-").toList match {
      case year :: month :: day :: Nil =>
        Today(year.toInt, month.toInt, day.toInt)
    }

    // Month value is 0-based. e.g., 0 for January.
    calendar.set(today.year, today.month - 1, today.date)

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    MyCalendar(today, today.monthDays, dayOfWeek)
  }

  /**
    * to get the day of week about the first day in the month
    * @param dayOfWeek the day of week about the certain @date,
    *                  which starts from 1. e.g., 1 for Sunday and 5 for Friday
    * @param date a certain date
    * @return the day of week about the first day in the month
    */
  @VisibleForTesting
  private[kang] def getFirstDayOfWeek(dayOfWeek: Int, date: Int): Int =
    ((dayOfWeek - date % 7) + 7) % 7 + 1

  def generateHtml(myCalendar: MyCalendar): String = {

    val year = myCalendar.today.year
    val month = myCalendar.today.month
    val date = myCalendar.today.date
    val dayOfWeek = myCalendar.dayOfWeek

    val firstDayOfWeek = getFirstDayOfWeek(dayOfWeek, date)

    val htmlDaysBuffer = new StringBuilder

    for (day <- 1 until (myCalendar.monthDays + firstDayOfWeek)) {
      if (day < firstDayOfWeek)
        htmlDaysBuffer ++= "  <li></li>\n"
      else if (day == (date + firstDayOfWeek - 1))
        htmlDaysBuffer ++= s"""  <li><span class=\"active\">$date</span></li>\n"""
      else htmlDaysBuffer ++= s"  <li>${day - (firstDayOfWeek - 1)}</li>\n"
    }

    s"""
      |<!DOCTYPE html>
      |<html>
      |<head>
      |
      |<style>
      |* {box-sizing: border-box;}
      |ul {list-style-type: none;}
      |body {font-family: Verdana, sans-serif;}
      |
      |.month {
      |    padding: 70px 25px;
      |    width: 100%;
      |    background: #1abc9c;
      |    text-align: center;
      |}
      |
      |.month ul {
      |    margin: 0;
      |    padding: 0;
      |}
      |
      |.month ul li {
      |    color: white;
      |    font-size: 20px;
      |    text-transform: uppercase;
      |    letter-spacing: 3px;
      |}
      |
      |.month .prev {
      |    float: left;
      |    padding-top: 10px;
      |}
      |
      |.month .next {
      |    float: right;
      |    padding-top: 10px;
      |}
      |
      |.weekdays {
      |    margin: 0;
      |    padding: 10px 0;
      |    background-color: #ddd;
      |}
      |
      |.weekdays li {
      |    display: inline-block;
      |    width: 13.6%;
      |    color: #666;
      |    text-align: center;
      |}
      |
      |.days {
      |    padding: 10px 0;
      |    background: #eee;
      |    margin: 0;
      |}
      |
      |.days li {
      |    list-style-type: none;
      |    display: inline-block;
      |    width: 13.6%;
      |    text-align: center;
      |    margin-bottom: 5px;
      |    font-size:12px;
      |    color: #777;
      |}
      |
      |.days li .active {
      |    padding: 5px;
      |    background: #1abc9c;
      |    color: white !important
      |}
      |
      |/* Add media queries for smaller screens */
      |@media screen and (max-width:720px) {
      |    .weekdays li, .days li {width: 13.1%;}
      |}
      |
      |@media screen and (max-width: 420px) {
      |    .weekdays li, .days li {width: 12.5%;}
      |    .days li .active {padding: 2px;}
      |}
      |
      |@media screen and (max-width: 290px) {
      |    .weekdays li, .days li {width: 12.2%;}
      |}
      |</style>
      |</head>
      |<body>
      |
      |<h1>My 0.0.1 Calendar</h1>
      |
      |<div class="month">
      |    <ul>
      |        <li class="prev">&#10094;</li>
      |        <li class="next">&#10095;</li>
      |        <li>
      |            $month 月<br>
      |            <span style="font-size:18px">$year</span>
      |        </li>
      |    </ul>
      |</div>
      |
      |<ul class="weekdays">
      |    <li>Su</li>
      |    <li>Mo</li>
      |    <li>Tu</li>
      |    <li>We</li>
      |    <li>Th</li>
      |    <li>Fr</li>
      |    <li>Sa</li>
      |</ul>
      |
      |<ul class="days">
      |  $htmlDaysBuffer
      |</ul>
      |
      |</body>
      |</html>
      |
    """.stripMargin

  }
}

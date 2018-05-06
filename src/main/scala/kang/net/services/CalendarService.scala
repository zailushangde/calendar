package kang.net.services

import java.text.SimpleDateFormat
import java.util.Calendar

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
      case year :: month :: day :: Nil => Today(year.toInt, month.toInt, day.toInt)
    }

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    MyCalendar(today, today.monthDays, dayOfWeek)
  }

  def generateHtml(myCalendar: MyCalendar): String = {

    val year = myCalendar.today.year
    val month = myCalendar.today.month
    val today = myCalendar.today.day

    val firstDayOfWeek = myCalendar.dayOfWeek - (myCalendar.today.day - 1) + 7

    val buffer = new StringBuilder
    for (day <- 1 until myCalendar.monthDays + firstDayOfWeek) {
      if (day < firstDayOfWeek)
        buffer ++= "  <li></li>\n"
      else if (day == (today + firstDayOfWeek - 1)) buffer ++= s"""  <li><span class=\"active\">$today</span></li>\n"""
      else buffer ++= s"  <li>${day - (firstDayOfWeek - 1)}</li>\n"
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
      |            $month<br>
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
      |  $buffer
      |</ul>
      |
      |</body>
      |</html>
      |
    """.stripMargin

  }
}

package kang.net.services

import kang.net.model.Today

trait CalendarService {
  def generateCalendarHtml(today: Today): String
}

class CalendarServiceImpl extends CalendarService {

  def generateCalendarHtml(today: Today): String = {

    val date = today.date
    val firstDayOfWeek = today.getFirstDayOfWeek
    val htmlDaysBuffer = new StringBuilder

    for (day <- 1 until (today.getDaysInTheMonth + firstDayOfWeek)) {
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
      |            ${today.month} 月<br>
      |            <span style="font-size:18px">${today.year}</span>
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

package kang.net

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import kang.net.controllers.CalendarController
import kang.net.services.CalendarServiceImpl
import kang.net.utils.JsonMapping

object AkkaHttpService extends App with JsonMapping {

  implicit val system = ActorSystem("calendar-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val calendarService = new CalendarServiceImpl
  val calendarController = new CalendarController(calendarService)

  val routers: Route = calendarController.route
  Http().bindAndHandle(routers, "0.0.0.0", 9000)
}
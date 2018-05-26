package kang.net

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import kang.net.controllers.CalendarController
import kang.net.persistence.DatabaseProvider
import kang.net.services.CalendarServiceImpl
import kang.net.utils.config.AppConfig

object AkkaHttpService extends App {

  implicit val system = ActorSystem("calendar-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val calendarService = new CalendarServiceImpl
  val calendarController = new CalendarController(calendarService)

  val dbProvider = new DatabaseProvider

  val routers: Route = calendarController.route
  Http().bindAndHandle(routers, "0.0.0.0", 9000)
}

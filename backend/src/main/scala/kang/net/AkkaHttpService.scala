package kang.net

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import kang.net.controllers.{CalendarController, EventController}
import kang.net.persistence.{DatabaseProvider, EventMauricioDao}
import kang.net.utils.config.AppConfig
import akka.http.scaladsl.server.Directives._
import org.slf4j.LoggerFactory

object AkkaHttpService extends App {

  val logger = LoggerFactory.getLogger(this.getClass)

  implicit val system = ActorSystem("calendar-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val calendarController = new CalendarController()

  val dbProvider = new DatabaseProvider(AppConfig.getConfig.database)

  dbProvider.getMauricioProvider().connect.map { connect =>
    val eventDao = new EventMauricioDao(connect)
    val eventController = new EventController(eventDao)

    val routers: Route = calendarController.route ~ eventController.route
    Http().bindAndHandle(routers, "0.0.0.0", 9001)
  }
}

package kang.net.utils

import akka.http.scaladsl.model.headers.Allow
import akka.http.scaladsl.server.Directives.{complete, options, respondWithHeader}
import akka.http.scaladsl.server.{MethodRejection, RejectionHandler}
import akka.http.scaladsl.server.Directives._

object MyExceptionHandler {

  implicit def rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handleAll[MethodRejection] { rejections =>
        val methods    = rejections map (_.supported)
        lazy val names = methods map (_.name) mkString ", "

        respondWithHeader(Allow(methods)) {
          options {
            complete(s"Supported methods : $names.")
          } ~
            complete(405, s"HTTP method not allowed, 嘻嘻嘻 supported methods: $names!")
        }
      }
      .result()
}

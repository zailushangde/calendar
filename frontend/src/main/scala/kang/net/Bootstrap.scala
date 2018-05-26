package kang.net

import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom
import dom.document
import org.querki.jquery._

import fr.hmil.roshttp.HttpRequest
import monix.execution.Scheduler.Implicits.global
import scala.util.{Failure, Success}
import fr.hmil.roshttp.response.SimpleHttpResponse

object Bootstrap {

  def main(args: Array[String]): Unit = {
    println("hello")
    $(() => setupUI())
  }

  def setupUI(): Unit = {
    $("body").append("<p>Hello hhh World</p>")
    $("#click-me-button").click(() => addClickedMessage())
    $("#click-me-button").click(() => getToday())
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  @JSExportTopLevel("addClickedMessage")
  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the button!")
  }

  @JSExportTopLevel("getToday")
  def getToday(): Unit = {
    println("hello iam here")
    val request = HttpRequest("http://localhost:9000/api/calendars")
    request.send().onComplete {
      case res: Success[SimpleHttpResponse] => appendPar(document.body, res.get.body)
      case _: Failure[SimpleHttpResponse] => println("there is a failure")
    }
  }
}

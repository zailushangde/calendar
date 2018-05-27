package kang.net.persistence

import java.time.format.DateTimeFormatter

import com.github.mauricio.async.db.{Connection, QueryResult, RowData}
import kang.net.model.{Event, Today}
import java.time.{ZoneOffset, ZonedDateTime}

import org.joda.time.LocalDateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EventDao {
  def getEventsInTheMonth(today: Option[Today] = None): Future[IndexedSeq[Event]]

  def insertEvent(event: Event): Future[Int]

  def getEventById(id: Int): Future[Option[Event]]
}

class EventMauricioDao(dbConnection: Connection) extends EventDao {

  // TODO simplify the logic with case class Today
  override def getEventsInTheMonth(today: Option[Today] = None): Future[IndexedSeq[Event]] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val now = ZonedDateTime.now(ZoneOffset.UTC)

    val localDate = now.toLocalDate
    val start = localDate
                  .withDayOfMonth(1)
                  .atStartOfDay
                  .format(formatter)

    val end = localDate
                .withDayOfMonth(localDate.getMonth.length(localDate.isLeapYear))
                .plusDays(1)
                .atStartOfDay()
                .format(formatter)

    dbConnection
      .sendPreparedStatement(EventQueries.select, Array(start.toString, end.toString))
      .map(queryRes => queryRes.rows.get.map(rowToEvent))
  }

  override def insertEvent(event: Event): Future[Int] = {
    event.id match {
      case Some(id) => update(event, id)
      case _ => insert(event)
    }
  }

  override def getEventById(id: Int): Future[Option[Event]] = {
    dbConnection
      .sendPreparedStatement(EventQueries.selectById, Array(id))
      .map(_.rows.get.map(rowToEvent).headOption)
  }

  private def insert(event: Event): Future[Int] = {
    dbConnection
      .sendPreparedStatement(EventQueries.insert,
        Array(event.title, event.description, event.eventStart, event.eventEnd))
      .map(_.rows.get(0)("id").asInstanceOf[Int])
  }

  private def update(event: Event, id: Int): Future[Int] = {
    dbConnection
      .sendPreparedStatement(EventQueries.update,
        Array(event.title, event.description, event.eventStart, event.eventEnd, id))
      .map(_ => id)
  }

  private def rowToEvent(row: RowData): Event = {
      Event(
        id = Some(row("id").asInstanceOf[Int]),
        title = row("title").asInstanceOf[String],
        description = row("description").asInstanceOf[String],
        eventStart = row("event_start").asInstanceOf[LocalDateTime],
        eventEnd = row("event_end").asInstanceOf[LocalDateTime]
      )
  }
}
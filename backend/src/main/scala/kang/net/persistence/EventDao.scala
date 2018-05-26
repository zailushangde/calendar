package kang.net.persistence

import java.time.format.DateTimeFormatter

import com.github.mauricio.async.db.{Connection, RowData}
import kang.net.model.{Event, Today}
import java.time.{ZoneOffset, ZonedDateTime}
import org.joda.time.LocalDateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait EventDao {
  def getEventsInTheMonth(today: Option[Today] = None): Future[IndexedSeq[Event]]

  def insertEvent(event: Event)
}

class EventMauricioDao(dbConnection: Connection) extends EventDao {

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

  override def insertEvent(event: Event): Unit = {

  }

  private def rowToEvent(row: RowData): Event = {
      Event(
        id = row("id").asInstanceOf[Int],
        title = row("title").asInstanceOf[String],
        description = row("description").asInstanceOf[String],
        eventStart = row("event_start").asInstanceOf[LocalDateTime],
        eventEnd = row("event_end").asInstanceOf[LocalDateTime]
      )
  }
}
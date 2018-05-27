package kang.net.persistence

import kang.net.common.DatabaseSpecHelper._
import kang.net.model.Event
import org.joda.time.LocalDateTime
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, Matchers}

class EventDaoSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    startPostgresDocker()
    migrateDb()
  }

  override def afterAll(): Unit = {
    removeSchema()
    stopPostgresDocker()
  }

  lazy val testDao = new EventMauricioDao(dbConnection)

  private val testEvent = Event(None, "title_test", "des_test", LocalDateTime.now(), LocalDateTime.now())

  private var testId = 0

  "EventDao" should "be able to query event in the whole month" in {
    testDao.getEventsInTheMonth().map { eventList =>
      assert(eventList.size == 2)
    }
  }

  it should "be able to insert new event" in {
    testDao.insertEvent(testEvent).map { id =>
      testId = id
      assert(id.isInstanceOf[Int])
    }
  }

  it should "be able to update event" in {
    testDao.insertEvent(testEvent.copy(id = Some(testId))).map { id =>
      assert(id == testId)
    }
  }
}

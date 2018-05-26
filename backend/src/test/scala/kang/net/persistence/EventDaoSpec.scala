package kang.net.persistence

import kang.net.common.DatabaseSpecHelper._
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

  "EventDao" should "be able to query event in the whole month" in {
    testDao.getEventsInTheMonth().map { eventList =>
      eventList.foreach(println)
      assert(eventList.size == 2)
    }
  }
}

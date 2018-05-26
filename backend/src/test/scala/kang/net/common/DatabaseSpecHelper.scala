package kang.net.common

import com.github.mauricio.async.db.Connection
import kang.net.persistence.DatabaseProvider
import kang.net.utils.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer

import scala.concurrent.Await
import scala.concurrent.duration._

object DatabaseSpecHelper {
  private lazy val postgres: PostgreSQLContainer[Nothing] = new PostgreSQLContainer()
  private lazy val flyway = new Flyway

  private lazy val dbConfig = DatabaseConfig(postgres.getJdbcUrl, postgres.getUsername, postgres.getPassword)

  private lazy val handler = new DatabaseProvider().getMauricioProvider(dbConfig)

  def dbConnection: Connection = {
    Await.result(handler.connect, 5.seconds)
  }

  def startPostgresDocker(): Unit = {
    postgres.start()
  }

  def stopPostgresDocker(): Unit = {
    postgres.stop()
  }

  def migrateDb(): Int = {
    flyway.setDataSource(dbConfig.url, dbConfig.user, dbConfig.password)
    flyway.migrate()
  }

  def removeSchema(): Unit = {
    flyway.clean()
  }
}

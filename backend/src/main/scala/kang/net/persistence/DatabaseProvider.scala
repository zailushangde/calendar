package kang.net.persistence

import com.github.mauricio.async.db.Connection
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import kang.net.utils.config.DatabaseConfig

class DatabaseProvider(dbConfig: DatabaseConfig) {

  def getMauricioProvider(): Connection = {
      val url = dbConfig.url + s"?user=${dbConfig.user}&" + s"password=${dbConfig.password}"
      val configuration = URLParser.parse(url)
      new PostgreSQLConnection(configuration)
  }
}

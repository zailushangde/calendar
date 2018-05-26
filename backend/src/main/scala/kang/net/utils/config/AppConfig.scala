package kang.net.utils.config

import pureconfig.error.ConfigReaderFailures

case class AppConfig(database: DatabaseConfig)

object AppConfig {

  def getAppConfig: Either[ConfigReaderFailures, AppConfig] = {
    pureconfig.loadConfig[AppConfig]
  }
}

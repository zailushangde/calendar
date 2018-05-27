package kang.net.utils.config

import pureconfig.error.{ConfigReaderException, ConfigReaderFailures}

case class AppConfig(database: DatabaseConfig)

object AppConfig {

  private def getAppConfig: Either[ConfigReaderFailures, AppConfig] = {
    pureconfig.loadConfig[AppConfig]
  }

  def getConfig: AppConfig = {
    getAppConfig match {
      case Right(config) => config
      case Left(failures) => throw new ConfigReaderException[AppConfig](failures)
    }
  }
}

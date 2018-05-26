package kang.net.utils.config

case class AppConfig(database: DatabaseConfig)

object AppConfig {
  pureconfig.loadConfig[AppConfig]
}

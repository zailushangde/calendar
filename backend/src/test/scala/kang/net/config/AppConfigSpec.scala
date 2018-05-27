package kang.net.config

import kang.net.utils.config.AppConfig
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}
import pureconfig.error.ConfigReaderFailures

class AppConfigSpec extends FlatSpec with Matchers with PrivateMethodTester {

  "AppConfig" should "be loaded successfully" in {
    val getAppConfig = PrivateMethod[Either[ConfigReaderFailures, AppConfig]]('getAppConfig)
    (AppConfig invokePrivate getAppConfig()).isRight shouldBe true
  }
}

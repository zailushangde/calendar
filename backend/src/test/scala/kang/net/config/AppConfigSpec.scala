package kang.net.config

import kang.net.utils.config.AppConfig
import org.scalatest.{FlatSpec, Matchers}

class AppConfigSpec extends FlatSpec with Matchers {

  "AppConfig" should "be loaded successfully" in {
    AppConfig.getAppConfig.isRight shouldBe true
  }
}

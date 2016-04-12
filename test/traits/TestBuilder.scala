package traits

import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Mode}
import scala.concurrent.duration._

/**
  * Created by mattia on 12/04/16.
  */
trait TestBuilder extends FlatSpec with ScalaFutures with Matchers {
  def builder: GuiceApplicationBuilder = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .loadConfig(Configuration(ConfigFactory.load("application.test.conf")))

  implicit val p = PatienceConfig(timeout = 30 seconds)
  //implicit val t: Timeout = 30 seconds
}

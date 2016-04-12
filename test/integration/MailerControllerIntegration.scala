package integration

import mocks.{AuthServiceMock, Samples}
import models.Formatters._
import org.scalatestplus.play.OneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{MailServiceMock, AuthService, MailService}
import traits.TestBuilder

/**
  * Created by mattia on 12/04/16.
  */
class MailerControllerIntegration extends TestBuilder with OneAppPerSuite {
  implicit override lazy val app: Application = builder
    .overrides(bind[AuthService].to[AuthServiceMock])
    .overrides(bind[MailService].to[MailServiceMock])
    .build()

  it should "not send email without authorization" in {
    val request = FakeRequest(POST, "/send")
      .withJsonBody(Json.toJson(Samples.sendmailAction.copy(auth = AuthServiceMock.invalidBulkEmailAuth)))

    val response = route(app, request).get

    status(response) shouldBe FORBIDDEN
  }

  it should "send email with authorization" in {
    val request = FakeRequest(POST, "/send")
      .withJsonBody(Json.toJson(Samples.sendmailAction.copy(auth = AuthServiceMock.validBulkEmailAuth)))

    val response = route(app, request).get

    status(response) shouldBe OK
  }

}

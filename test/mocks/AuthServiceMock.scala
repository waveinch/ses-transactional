package mocks

import javax.inject.Inject

import models.BulkMailAuth
import play.api.libs.concurrent.Execution.Implicits._
import services.AuthService

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class AuthServiceMock @Inject()() extends AuthService {
  override def isAuthorized(auth: BulkMailAuth): Future[Boolean] = Future {
    auth match {
      case AuthServiceMock.validBulkEmailAuth => true
      case _ => false
    }
  }
}

object AuthServiceMock {
  val validBulkEmailAuth = BulkMailAuth("ok", "ok", "ok")
  val invalidBulkEmailAuth = BulkMailAuth("ko", "ko", "ko")
}

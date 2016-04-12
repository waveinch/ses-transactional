package mocks

import javax.inject.Inject

import models.BulkEmailAuth
import play.api.libs.concurrent.Execution.Implicits._
import services.AuthService

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class AuthServiceMock @Inject()() extends AuthService {
  override def isAuthorized(auth: BulkEmailAuth): Future[Boolean] = Future {
    auth match {
      case AuthServiceMock.validBulkEmailAuth => true
      case _ => false
    }
  }
}

object AuthServiceMock {
  val validBulkEmailAuth = BulkEmailAuth("ok", "ok", "ok")
  val invalidBulkEmailAuth = BulkEmailAuth("ko", "ko", "ko")
}

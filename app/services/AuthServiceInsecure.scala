package services

import models.BulkMailAuth

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 06/05/16.
  */
class AuthServiceInsecure extends AuthService {
  override def isAuthorized(auth: BulkMailAuth): Future[Boolean] = Future{true}
}

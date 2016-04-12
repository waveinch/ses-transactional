package services

import models.BulkEmailAuth

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
trait AuthService {
  def isAuthorized(auth: BulkEmailAuth): Future[Boolean]
}

package services

import models.BulkMailAuth

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
trait AuthService {
  def isAuthorized(auth: BulkMailAuth): Future[Boolean]
}

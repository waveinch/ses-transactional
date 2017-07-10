package services

import models.{Mail, MailStatus, Quota}

import scala.concurrent.Future

/**
  * Created by unoedx on 10/04/16.
  */
trait MailService {
  def send(mail:Mail):Future[MailStatus]

  def quota():Future[Quota]

  def sandbox:Boolean

}

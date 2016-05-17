package services

import com.amazonaws.services.simpleemail.model.GetSendQuotaResult
import models.{Mail, MailStatus}

import scala.concurrent.Future

/**
  * Created by unoedx on 10/04/16.
  */
trait MailService {
  def send(mail:Mail):Future[MailStatus]

  def quota():Future[GetSendQuotaResult]

  def sandbox:Boolean

}

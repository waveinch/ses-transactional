package mocks

import javax.inject.Inject

import models.{Mail, MailStatus, Quota}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import services.MailService

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class MailServiceMock @Inject()() extends MailService {
  override def sandbox: Boolean = true

  override def send(mail: Mail): Future[MailStatus] = Future {
    mail.to match {
      case MailServiceMock.invalidTo => {
        Logger.info(s"[REJECT] MailServiceMock.send --- ${mail.from}->${mail.to}: [${mail.title}] ${mail.text} -- ${mail.html}")
        MailStatus(mail.to,"id", false, Some("Invalid 'to' field"))
      }
      case _ => {
        Logger.info(s"[OK] MailServiceMock.send --- ${mail.from}->${mail.to}: [${mail.title}] ${mail.text} -- ${mail.html}")
        MailStatus(mail.to,"id", true, None)
      }
    }
  }

  override def quota(): Future[Quota] = Future{
    Quota(1000,5)
  }
}

object MailServiceMock {
  val invalidTo = "invalid@email.ch"
}

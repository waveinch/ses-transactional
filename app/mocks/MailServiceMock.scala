package mocks

import javax.inject.Inject

import com.amazonaws.services.simpleemail.model.GetSendQuotaResult
import models.{Mail, MailStatus}
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

  override def quota(): Future[GetSendQuotaResult] = Future{
    val quota = new GetSendQuotaResult()

    quota.setMax24HourSend(1000.0)
    quota.setMaxSendRate(5.0)

    quota
  }
}

object MailServiceMock {
  val invalidTo = "invalid@email.ch"
}

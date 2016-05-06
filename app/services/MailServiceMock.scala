package services

import javax.inject.Inject

import com.amazonaws.services.simpleemail.model.GetSendQuotaResult
import models.{Mail, MailStatus}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class MailServiceMock @Inject()() extends MailService {
  override def send(mail: Mail): Future[MailStatus] = Future {
    mail.to match {
      case MailServiceMock.invalidTo => {
        Logger.info(s"[REJECT] MailServiceMock.send --- ${mail.from}->${mail.to}: [${mail.title}] ${mail.text} -- ${mail.html}")
        MailStatus(mail.to, false, Some("Invalid 'to' field"))
      }
      case _ => {
        Logger.info(s"[OK] MailServiceMock.send --- ${mail.from}->${mail.to}: [${mail.title}] ${mail.text} -- ${mail.html}")
        MailStatus(mail.to, true, None)
      }
    }
  }

  override def quota(): Future[GetSendQuotaResult] = Future{
    val quota = new GetSendQuotaResult()

    quota.setMax24HourSend(100.0)
    quota.setMaxSendRate(10.0)

    quota
  }
}

object MailServiceMock {
  val invalidTo = "invalid@email.ch"
}

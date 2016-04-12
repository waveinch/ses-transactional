package mocks

import javax.inject.Inject

import models.MailStatus
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import services.MailService

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class MailServiceMock @Inject()() extends MailService {
  override def send(from: String, to: String, title: String, text: String, html: String): Future[MailStatus] = Future {
    to match {
      case MailServiceMock.invalidTo => {
        Logger.info(s"[REJECT] MailServiceMock.send --- $from->$to: [$title] $text -- $html")
        MailStatus(to, false, Some("Invalid 'to' field"))
      }
      case _ => {
        Logger.info(s"[OK] MailServiceMock.send --- $from->$to: [$title] $text -- $html")
        MailStatus(to, true, None)
      }
    }
  }
}

object MailServiceMock {
  val invalidTo = "invalid@email.ch"
}

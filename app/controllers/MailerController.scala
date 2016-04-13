package controllers

import javax.inject.Inject

import models.Formatters._
import models.{Formatters, MailStatus, BulkMail, SendMailAction}
import play.api.libs.json.Json
import play.api.mvc._
import services.{AuthService, MailService, Template}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by unoedx on 09/04/16.
  */
class MailerController @Inject()(
                                  authService: AuthService,
                                  mailer: MailService) extends Controller {

  def send() = Action.async(parse.json(maxLength = 100 * 1024 * 1024)) { r =>
    val sendMailAction = r.body.as[SendMailAction](Formatters.sendMailAction)
    val bulkMail = sendMailAction.bulkMail
    val auth = sendMailAction.auth

    val result = for {
      isAuthorized <- authService.isAuthorized(auth) if isAuthorized
      mailReports <- sendMails(bulkMail)
    } yield Ok(Json.obj("result" -> mailReports))

    result recover {
      case _ => Forbidden
    }

  }

  private def sendMails(bulkMail: BulkMail): Future[Boolean] = {
    Future.sequence {
      bulkMail.mails.map { mail =>
        mailer.send(
          from = bulkMail.fromEmail,
          to = mail.email,
          title = bulkMail.subject,
          text = Template.render(bulkMail.text, mail.paramsWithMail),
          html = Template.render(bulkMail.html, mail.paramsWithMail)
        )
      }
    }

    Future.successful(true)
  }


}

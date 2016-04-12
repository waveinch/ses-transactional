package controllers

import javax.inject.Inject

import models.Formatters._
import models.SendMailAction
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

  def send() = Action.async(parse.json[SendMailAction]) { r =>
    val sendMailAction = r.body
    val bulkMail = sendMailAction.bulkMail
    val auth = sendMailAction.auth

    val result = for {
      isAuthorized <- authService.isAuthorized(auth) if isAuthorized
      sentMails = bulkMail.mails.map { mail =>
        mailer.send(
          from = bulkMail.fromEmail,
          to = mail.email,
          title = bulkMail.subject,
          text = Template.render(bulkMail.text, mail.paramsWithMail),
          html = Template.render(bulkMail.html, mail.paramsWithMail)
        )
      }
      mailReports <- Future.sequence(sentMails)
    } yield Ok(Json.toJson(mailReports))

    result recover {
      case _ => Forbidden
    }

  }


}

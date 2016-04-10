package controllers

import javax.inject.Inject

import models.{MailStatus, MailParams, BulkMail}
import play.api.libs.json.Json
import play.api.mvc._
import services.{Template, MailService}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 09/04/16.
  */
class MailerController @Inject()(mailer: MailService) extends Controller  {

    implicit val mailParamFormatter = Json.format[MailParams]
    implicit val bulkFormatter = Json.format[BulkMail]
    implicit val mailStatusFormatter = Json.format[MailStatus]

    def send() = Action.async(parse.json[BulkMail]) { r =>
        val bulkMail = r.body
        val sentMails = bulkMail.mails.map{ mail =>
           mailer.send(
            from = bulkMail.fromEmail,
            to = mail.email,
            title = bulkMail.subject,
            text = Template.render(bulkMail.text,mail.paramsWithMail),
            html = Template.render(bulkMail.html,mail.paramsWithMail)
           )
        }
        Future.sequence(sentMails).map{ mails =>
            Ok(Json.toJson(mails))
        }

    }



}

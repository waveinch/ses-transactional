package controllers

import javax.inject.{Inject,Named}

import akka.actor.ActorRef
import models.Formatters._
import models._
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import sender.{Messages, CampaignSupervisor}
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by unoedx on 09/04/16.
  */
class MailerController @Inject()(
                                  authService: AuthService,
                                  mailer: MailService,
                                  configuration: Configuration,
                                  feedbackService: FeedbackService,
                                  @Named(CampaignSupervisor.name) campaignSupervisor: ActorRef
                                ) extends Controller {
  /**
    *
    * Example:
    * {
        "auth": {
            "partnerId": "123",
               "apiKey": "123",
               "campaignId": "123"
          },
          "bulkMail": {
            "subject": "Test",
              "html":"HTML Test",
              "text":"Text Test",
              "fromName":"Dev Wavein",
              "fromEmail":"dev@wavein.ch",
              "mails": [
                {
                "email": "andrea@wavien.ch",
                  "params": {
                    "uuid": "asfkjksd"
                  }
                }
              ]
          }
      }
    *
    * @return
    */
  def send() = Action.async(parse.json(maxLength = 100 * 1024 * 1024)) { r =>
    val sendMailAction = r.body.as[SendMailAction](Formatters.sendMailAction)

    println("Recived:")
    println(r.body)

    val bulkMail = sendMailAction.bulkMail
    val auth = sendMailAction.auth

    val result = for {
      isAuthorized <- authService.isAuthorized(auth) if isAuthorized
      quota <- mailer.quota()
    } yield {

      val testerParams = testers.map(m => MailParams(m, Map("uuid" -> "UUID-key")))
      val bm = if(auth.campaignId.contains("inviotest")) {
        bulkMail.copy(mails = testerParams)
      } else {
        bulkMail.copy(mails = testerParams ++ bulkMail.mails)
      }

      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,bm,quota)
      Ok(Json.obj("result" -> true))
    }

    result recover {
      case _ => Forbidden
    }

  }


  def sandboxSuccess(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,Sandbox.bulkSuccess(num),quota)
      Ok("Campaign submitted")
    }
  }

  def sandboxBounce(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,Sandbox.bulkBounce(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxComplaint(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,Sandbox.bulkComplaint(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxOOTO(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,Sandbox.bulkOoto(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxSuppression(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(testers,mailer,feedbackService,Sandbox.bulkSuppression(num),quota)
      Ok("Campaign submitted")
    }
  }




  def bounce() = Action.async{ r =>
    val msg = message(r.body.asText.get)
    val feedback = Feedback.fromMessage(msg,"bounce")
    println("bounce:" + msg)
    feedbackService.bounces(feedback).map{_ => Ok("received") }
  }

  def complaint() = Action.async{ r =>
    val msg = message(r.body.asText.get)
    val feedback = Feedback.fromMessage(msg,"complaint")
    println("complaint:" + msg)
    feedbackService.bounces(feedback).map{_ => Ok("received") }
  }

  def success() = Action.async{ r =>
    val msg = message(r.body.asText.get)
    val feedback = Feedback.fromMessage(msg,"success")
    println("success:" + msg)
    feedbackService.delivery(feedback).map{_ => Ok("received") }
  }


  private val testers = configuration.getStringSeq("adt.testers").get

  private def message(body:String):JsValue = Json.parse((Json.parse(body) \ "Message").as[String])







}

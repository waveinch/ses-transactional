package controllers

import javax.inject.{Inject,Named}

import akka.actor.ActorRef
import models.Formatters._
import models._
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import sender.{Messages, CampaignSupervisor}
import services.{Hash, AuthService, MailService, Template}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by unoedx on 09/04/16.
  */
class MailerController @Inject()(
                                  authService: AuthService,
                                  conf: Configuration,
                                  mailer: MailService,
                                  wsClient: WSClient,
                                  @Named(CampaignSupervisor.name) campaignSupervisor: ActorRef
                                ) extends Controller {

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

      campaignSupervisor ! Messages.Campaign(mailer,bulkMail,quota)
      Ok(Json.obj("result" -> true))
    }

    result recover {
      case _ => Forbidden
    }

  }


  def sandboxSuccess(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkSuccess(num),quota)
      Ok("Campaign submitted")
    }
  }

  def sandboxBounce(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkBounce(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxComplaint(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkComplaint(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxOOTO(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkOoto(num),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxSuppression(num:Int) = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkSuppression(num),quota)
      Ok("Campaign submitted")
    }
  }



  implicit val feedbackFormatter = Json.format[Feedback]
  case class Feedback(
                              reason:String,
                              mailId:String,
                              fromMail:String,
                              hashedMail:Seq[String],
                              timestamp:String
                            )
  def bounce() = Action.async{ r =>
    val url = conf.getString("adt.frontend").get + "/newsletter/bounces"
    val msg = message(r.body.asText.get)
    val feedback = message2Feedback(msg,"bounce")
    println("bounce:" + msg)
    sendFeedback(feedback,url).map{_ => Ok("received") }
  }

  def complaint() = Action.async{ r =>
    val url = conf.getString("adt.frontend").get + "/newsletter/bounces"
    val msg = message(r.body.asText.get)
    val feedback = message2Feedback(msg,"complaint")
    println("complaint:" + msg)
    sendFeedback(feedback,url).map{_ => Ok("received") }
  }

  def success() = Action.async{ r =>
    val url = conf.getString("adt.frontend").get + "/newsletter/delivery"
    val msg = message(r.body.asText.get)
    val feedback = message2Feedback(msg,"success")
    println("success:" + msg)
    sendFeedback(feedback,url).map{_ => Ok("received") }
  }




  private def message(body:String):JsValue = Json.parse((Json.parse(body) \ "Message").as[String])

  private def message2Feedback(msg:JsValue,reason:String) = Feedback(
    reason = reason + (msg \ "bounce" \ "bounceType").asOpt[String].map(b => "-"+b).getOrElse(""),
    mailId = (msg \ "mail" \ "messageId").as[String],
    fromMail = (msg \ "mail" \ "source").as[String],
    hashedMail = (msg \ "mail" \ "destination").as[Seq[String]].map(Hash.hashEmail),
    timestamp = (msg \ "mail" \ "timestamp").as[String]
  )

  private def sendFeedback(feedback:Feedback,url:String):Future[Boolean] = {
     wsClient.url(url).post(Json.toJson(feedback)).map(_.status == 200)
  }

}

package controllers

import javax.inject.{Inject,Named}

import akka.actor.ActorRef
import models.Formatters._
import models._
import play.api.libs.json.Json
import play.api.mvc._
import sender.{Messages, CampaignSupervisor}
import services.{AuthService, MailService, Template}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by unoedx on 09/04/16.
  */
class MailerController @Inject()(
                                  authService: AuthService,
                                  mailer: MailService,
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


  def sandboxSuccess() = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkSuccess(1000),quota)
      Ok("Campaign submitted")
    }
  }

  def sandboxBounce() = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkBounce(10),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxComplaint() = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkComplaint(10),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxOOTO() = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkOoto(10),quota)
      Ok("Campaign submitted")
    }
  }
  def sandboxSuppression() = Action.async{
    mailer.quota().map{ quota =>
      campaignSupervisor ! Messages.Campaign(mailer,Sandbox.bulkSuppression(10),quota)
      Ok("Campaign submitted")
    }
  }

  def complaint() = Action{ r =>
    println("Complaint:" + r.body)
    Ok("received")
  }

  def bounce() = Action{ r =>
    println("Bounce:" + r.body)
    Ok("received")
  }

  def success() = Action{ r =>
    println("Success:" + r.body)
    Ok("received")
  }


}

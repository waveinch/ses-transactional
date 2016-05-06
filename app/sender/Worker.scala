package sender

import akka.actor.{PoisonPill, Actor}
import akka.actor.Actor.Receive
import models.{MailParams, Mail, BulkMail}
import sender.Messages.{WorkDone, Tick, Job}
import services.{MailService, Template}

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by unoedx on 06/05/16.
  */
class Worker extends Actor {

  var currentBulk:BulkMail = null
  var currentMailer:MailService = null

  override def receive: Receive = {
    case Job(mailer,bulk) => {
      currentBulk = bulk
      currentMailer = mailer
      context.become(working)
    }
  }

  def working:Receive = {
    case Tick => processNextMail()
  }

  def finished:Receive = {
    case _ =>
  }

  private def processNextMail() = {
      if(currentBulk.mails.isEmpty) {
        context.parent ! WorkDone
        context.become(finished)
        self ! PoisonPill
      } else {
        val params = currentBulk.mails.head
        sendMail(params,currentBulk)
        currentBulk = currentBulk.copy(mails = currentBulk.mails.tail)
      }
  }

  private def sendMail(mail: MailParams, bulkMail: BulkMail) = {
        currentMailer.send( Mail(
          from = bulkMail.fromEmail,
          to = mail.email,
          title = bulkMail.subject,
          text = Template.render(bulkMail.text, mail.paramsWithMail),
          html = Template.render(bulkMail.html, mail.paramsWithMail)
        ) ).map{ status =>
          context.parent ! Messages.SentMail(status,bulkMail.fromEmail,mail.email)
        }
  }
}

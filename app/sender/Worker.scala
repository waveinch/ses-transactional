package sender

import akka.actor.{PoisonPill, Actor}
import akka.actor.Actor.Receive
import models.{Sandbox, MailParams, Mail, BulkMail}
import sender.Messages.{WorkDone, Tick, Job}
import services._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by unoedx on 06/05/16.
  */
class Worker extends Actor {

  var currentBulk:BulkMail = null
  var currentMailer:MailService = null
  var currentFeedback:FeedbackService = null

  override def receive: Receive = {
    case Job(mailer,feedback,bulk) => {
      currentBulk = bulk
      currentMailer = mailer
      currentFeedback = feedback
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

        val email = if(currentMailer.sandbox) {
          if(
              mail.email == Sandbox.bounceAddress ||
              mail.email == Sandbox.complaintAddress ||
              mail.email == Sandbox.ootoAddress ||
              mail.email == Sandbox.suppressionlistAddress
          ) mail.email else Sandbox.successAddress
        } else mail.email

        val from = s"${bulkMail.fromName} <$email>"

        currentMailer.send( Mail(
          from = bulkMail.fromEmail,
          to = from,
          title = bulkMail.subject,
          text = Template.render(bulkMail.text, mail.paramsWithMail),
          html = Template.render(bulkMail.html, mail.paramsWithMail)
        ) ).map{ status =>

          currentFeedback.delivery(Feedback("pending" + status.rejectReason.map(x => "-"+x).getOrElse(""),status.mailId,bulkMail.fromEmail,Seq(Hash.hashEmail(email)),new java.util.Date().toString))

          context.parent ! Messages.SentMail(status,bulkMail.fromEmail)
        }
  }
}

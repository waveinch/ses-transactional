package sender

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import models.Mail
import sender.Messages.{Campaign, CampaignDone, Tick}

/**
  * Created by unoedx on 06/05/16.
  */

object CampaignSupervisor{
  final val name = "campaign-supervisor"
}

class CampaignSupervisor extends Actor {

  var currentCampaign:Option[Campaign] = None
  var queuedCampaigns:List[Campaign] = List()

  var workersSupervisor:ActorRef = context.actorOf(Props[WorkerSupervisor])

  override def receive: Receive = {
    case c:Campaign => {

      if(currentCampaign.isEmpty) {
        println("Start first campaign")
        currentCampaign = Some(c)
        workersSupervisor ! c
        sendStartCampaignNotification(c)
      } else {
        println("Queue campaign")
        queuedCampaigns = c :: queuedCampaigns
      }
    }
    case Tick => {
      workersSupervisor ! Tick
    }
    case CampaignDone => {
      println("Campaign Done")
      currentCampaign.foreach(c => sendEndCampaignNotification(c))
      nextCampaign()
    }
  }

  def nextCampaign() = {
    currentCampaign = None
    if(!queuedCampaigns.isEmpty) {
      println("Send next Campaign")
      val next = queuedCampaigns.head
      queuedCampaigns = queuedCampaigns.tail
      currentCampaign = Some(next)
      workersSupervisor ! next
    }
  }



  private def sendStartCampaignNotification(c:Campaign): Unit = {
    val body =
      s"""
         |<h2>Sending campaign ${c.bulk.subject} from ${c.bulk.fromName} (${c.bulk.fromEmail})</h2>
         |<p>
         |  Daily quota: ${c.quota.daily}
         |  Mail/second: ${c.quota.rate}
         |</p>
         |<p>You should receive another notification mail when the campaign is done</p>
       """.stripMargin

    for(mail <- c.testers) {
      c.mailer.send(Mail("admin@amicidelticino.ch",mail,"Start campaign sending","",body))
    }
  }

  private def sendEndCampaignNotification(c:Campaign): Unit = {
    val body =
      s"""
         |<h2>Campaign ${c.bulk.subject} from ${c.bulk.fromName} (${c.bulk.fromEmail}):</h2>
         |<h1>Done!</h1>
         |<p>
         |Attempted to send ${c.bulk.mails.length} mails
         |</p>
       """.stripMargin

    for(mail <- c.testers) {
      c.mailer.send(Mail("admin@amicidelticino.ch",mail,"Campaign sent","",body))
    }
  }


}

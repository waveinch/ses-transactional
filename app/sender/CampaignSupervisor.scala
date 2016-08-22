package sender

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import sender.Messages.{CampaignDone, Tick, Campaign}

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


}

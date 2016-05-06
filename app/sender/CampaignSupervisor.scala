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

  var campaigns:List[Campaign] = List()

  var workersSupervisor:ActorRef = context.actorOf(Props[WorkerSupervisor])

  override def receive: Receive = {
    case c:Campaign => {
      campaigns = c :: campaigns
      if(campaigns.length == 1) {
        nextCampaign()
      }
    }
    case Tick => {
      workersSupervisor ! Tick
    }
    case CampaignDone => nextCampaign()
  }

  def nextCampaign() = {
    if(!campaigns.isEmpty) {
      val next = campaigns.head
      campaigns = campaigns.tail
      workersSupervisor ! next
    }
  }


}

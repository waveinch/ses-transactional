package sender

import akka.actor.{Props, ActorRef, Actor}
import akka.actor.Actor.Receive
import sender.Messages.{Tick, WorkDone, Job, Campaign}

/**
  * Created by unoedx on 06/05/16.
  */
class WorkerSupervisor extends Actor {

  var workers:List[ActorRef] = List()

  override def receive: Receive = {
    case c: Campaign => {
      context.become(working)
      startJobs(c)
    }
  }

  def working:Receive = {
    case WorkDone => {
      workers = workers.filterNot(_ == sender())
      if(workers.isEmpty) {
        context.parent ! Messages.CampaignDone
        context.become(receive)
      }
    }
    case s:Messages.SentMail => println(s)
    case Tick => workers.foreach( _ ! Tick)
  }

  private def startJobs(c:Campaign) = {

    val workersCount = (1*c.quota.getMaxSendRate).toInt
    val mailChuncks = c.bulk.mails.grouped(math.ceil(c.bulk.mails.length/workersCount).toInt)

    for(mails <- mailChuncks) {
      val worker = context.actorOf(Props[Worker])
      workers = worker :: workers
      worker ! Job(c.mailer,c.bulk.copy(mails = mails))
      println("worker started")
    }

    println("SendRate: " + c.quota.getMaxSendRate + " started ")

  }

}

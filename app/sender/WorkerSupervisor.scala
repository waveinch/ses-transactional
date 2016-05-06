package sender

import akka.actor.{Props, ActorRef, Actor}
import akka.actor.Actor.Receive
import org.joda.time.DateTime
import sender.Messages.{Tick, WorkDone, Job, Campaign}

/**
  * Created by unoedx on 06/05/16.
  */
class WorkerSupervisor extends Actor {

  var dailyLimit:Int = 0

  var counter:List[DateTime] = List()

  def addToCounter() = {
    val yesterday = DateTime.now().minusDays(1)
    counter = DateTime.now() :: counter.filter(_.isAfter(yesterday))
  }

  def hasQuota = {
    println("24 limit: " + dailyLimit + ", counter:" + counter.length)
    counter.length < dailyLimit
  }



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
    case s:Messages.SentMail => {
      addToCounter()
      println(s)
    }
    case Tick => if(hasQuota) workers.foreach( _ ! Tick)
  }

  private def startJobs(c:Campaign) = {

    dailyLimit = c.quota.getMax24HourSend.toInt
    counter = List()

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

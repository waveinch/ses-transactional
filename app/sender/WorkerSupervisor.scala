package sender

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import models.Mail
import org.joda.time.DateTime
import sender.Messages.{Campaign, Job, Tick, WorkDone}

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

    println("Start Job with quota:")
    println("max 24h:" + c.quota.daily)
    println("ratio:" + c.quota.rate)
    println("mails:" + c.bulk.mails.length)

    dailyLimit = c.quota.daily
    counter = List()

    val workersCount = 1*c.quota.rate
    val mailsPerWorker = math.max(1,math.ceil(c.bulk.mails.length/workersCount).toInt)
    println(mailsPerWorker + " mail to be processed by each worker")
    val mailChuncks = c.bulk.mails.grouped(mailsPerWorker).toList.filter(_.length > 0)

    println(mailChuncks.length + "workers")


    for(mails <- mailChuncks) {
      println("Starting worker")
      val worker = context.actorOf(Props[Worker])
      workers = worker :: workers
      worker ! Job(c.mailer,c.feedback,c.bulk.copy(mails = mails))
      println("worker started")
    }

    println("SendRate: " + c.quota.rate + " started ")

  }




}

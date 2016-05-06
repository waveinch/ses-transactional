package sender

import javax.inject.{Named, Inject}

import akka.actor.{ActorSystem, ActorRef}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 06/05/16.
  */

trait Ticker

class TickerImpl @Inject()(
                      @Named(CampaignSupervisor.name) campaignSupervisor: ActorRef,
                      system: ActorSystem
                      ) extends Ticker {

  system.scheduler.schedule(0 seconds, 1 second, campaignSupervisor, Messages.Tick)

}

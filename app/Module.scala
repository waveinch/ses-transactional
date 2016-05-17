import akka.actor.Props
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import sender.{TickerImpl, Ticker, CampaignSupervisor}
import services._


/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[MailService]).to(classOf[SesService])
    bind(classOf[FeedbackService]).to(classOf[FeedbackServiceImpl])
    bind(classOf[AuthService]).to(classOf[AuthServiceInsecure])
    bind(classOf[Ticker]).to(classOf[TickerImpl]).asEagerSingleton()
    bindActor[CampaignSupervisor](CampaignSupervisor.name)
  }

}

import com.google.inject.AbstractModule
import mocks.{FeedbackServiceMock, MailServiceMock}
import play.api.libs.concurrent.AkkaGuiceSupport
import sender.{TickerImpl, Ticker, CampaignSupervisor}
import services._


class TestModule extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[MailService]).to(classOf[MailServiceMock])
    bind(classOf[FeedbackService]).to(classOf[FeedbackServiceMock])
    bind(classOf[AuthService]).to(classOf[AuthServiceInsecure])
    bind(classOf[Ticker]).to(classOf[TickerImpl]).asEagerSingleton()
    bindActor[CampaignSupervisor](CampaignSupervisor.name)
  }

}

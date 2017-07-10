package sender

import com.amazonaws.services.simpleemail.model.GetSendQuotaResult
import models.{BulkMail, MailStatus, Quota}
import services.{FeedbackService, MailService}

/**
  * Created by unoedx on 06/05/16.
  */
object Messages {
    case class Campaign(testers:Seq[String], mailer:MailService, feedback: FeedbackService, bulk:BulkMail,quota:Quota)

    case class Job(mailer:MailService, feedback: FeedbackService,bulk:BulkMail)

    case class SentMail(status:MailStatus,from:String)

    case object Tick

    case object WorkDone
    case object CampaignDone

}

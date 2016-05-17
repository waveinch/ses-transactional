package services

import javax.inject.Inject

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 17/05/16.
  */
trait FeedbackService {
    def delivery(feedback:Feedback):Future[Boolean]
    def bounces(feedback:Feedback):Future[Boolean]
}

class FeedbackServiceImpl @Inject()(wsClient: WSClient, conf:Configuration) extends FeedbackService {

  implicit val feedbackFormatter = Json.format[Feedback]



  override def delivery(feedback: Feedback): Future[Boolean] = send(feedback,conf.getString("adt.frontend").get + "/newsletter/delivery")

  override def bounces(feedback: Feedback): Future[Boolean] = send(feedback,conf.getString("adt.frontend").get + "/newsletter/bounces")

  def send(feedback: Feedback, url: String): Future[Boolean] = {
    wsClient.url(url).post(Json.toJson(feedback)).map(_.status == 200)
  }
}


case class Feedback(
                     reason:String,
                     mailId:String,
                     fromMail:String,
                     hashedMail:Seq[String],
                     timestamp:String
                   )



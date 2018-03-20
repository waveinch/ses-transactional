package services

import java.io.IOException

import javax.inject.Inject
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.simpleemail._
import com.amazonaws.services.simpleemail.model._
import com.amazonaws.regions._
import models.{Mail, MailStatus, Quota}
import play.api.{Configuration, Environment}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 09/04/16.
  */
class SesService  @Inject()(
                             conf: Configuration) extends MailService {

  def key = System.getenv("SES_KEY")
  def secret = System.getenv("SES_SECRET")

  def credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(key, secret))

  val client: AmazonSimpleEmailService = AmazonSimpleEmailServiceClient.builder()
    .withRegion(Regions.EU_WEST_1)
    .withCredentials(credentials)
    .build()



  @throws(classOf[IOException])
  def send(mail:Mail):Future[MailStatus] = Future{
    val destination: Destination = new Destination().withToAddresses(mail.to)
    val subject: Content = new Content().withData(mail.title)
    val textBody: Body = new Body().withHtml(new Content().withData(mail.html))
    val body: Body = new Body().withText(new Content().withData(mail.text))
    val message: Message = new Message().withSubject(subject).withBody(body).withBody(textBody)
    val request: SendEmailRequest = new SendEmailRequest().withSource(mail.from).withDestination(destination).withMessage(message)
    try {
      System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...")

      val result = client.sendEmail(request)
      MailStatus(mail.to,result.getMessageId,true)
    }
    catch {
      case ex: AmazonServiceException => {
        System.out.println("The email was not sent.")
        System.out.println("Service: " + ex.getServiceName + ", errorcode:" + ex.getErrorCode)
        MailStatus(mail.to,ex.getRequestId,false,Some(ex.getErrorCode))
      }
      case ex: Exception => {
        System.out.println("The email was not sent.")
        System.out.println("Error message: " + ex.getMessage)
        MailStatus(mail.to,"",false,Some(ex.getMessage))
      }
    }
  }

  override def quota(): Future[Quota] = Future{
    val sesQuota = client.getSendQuota
    val maxRate = conf.getInt("adt.maxRate").getOrElse(1000000)
    val rate = math.min(maxRate,sesQuota.getMaxSendRate.toInt)
    Quota(sesQuota.getMax24HourSend.toInt,rate)
  }

  override def sandbox: Boolean = conf.getBoolean("adt.sandbox").get
}

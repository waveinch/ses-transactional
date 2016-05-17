package services

import java.io.IOException
import javax.inject.Inject
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import com.amazonaws.services.simpleemail._
import com.amazonaws.services.simpleemail.model._
import com.amazonaws.regions._
import models.{Mail, MailStatus}
import play.api.{Configuration, Environment}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 09/04/16.
  */
class SesService  @Inject()(
                             conf: Configuration) extends MailService {

  def credentials:BasicAWSCredentials = new BasicAWSCredentials(System.getenv("SES_KEY"),System.getenv("SES_SECRET"))

  val REGION: Region = Region.getRegion(Regions.EU_WEST_1)
  val client: AmazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(credentials).withRegion(REGION)


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

  override def quota(): Future[GetSendQuotaResult] = Future{
     client.getSendQuota
  }

  override def sandbox: Boolean = conf.getBoolean("adt.sandbox").get
}

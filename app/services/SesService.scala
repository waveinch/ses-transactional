package services

import java.io.IOException
import javax.inject.Inject
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials}
import com.amazonaws.services.simpleemail._
import com.amazonaws.services.simpleemail.model._
import com.amazonaws.regions._
import models.MailStatus
import play.api.Environment

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 09/04/16.
  */
class SesService extends MailService {

  def credentials:BasicAWSCredentials = new BasicAWSCredentials(System.getenv("SES_KEY"),System.getenv("SES_SECRET"))


  @throws(classOf[IOException])
  def send(from:String,to:String,title:String, text:String, html:String):Future[MailStatus] = Future{
    val destination: Destination = new Destination().withToAddresses(to)
    val subject: Content = new Content().withData(title)
    val textBody: Body = new Body().withHtml(new Content().withData(html))
    val body: Body = new Body().withText(new Content().withData(text))
    val message: Message = new Message().withSubject(subject).withBody(body).withBody(textBody)
    val request: SendEmailRequest = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message)
    try {
      System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...")
      val client: AmazonSimpleEmailServiceClient = new AmazonSimpleEmailServiceClient(credentials)
      val REGION: Region = Region.getRegion(Regions.EU_WEST_1)
      client.setRegion(REGION)
      client.sendEmail(request)
      System.out.println("Email sent!")
      MailStatus(to,true)
    }
    catch {
      case ex: AmazonServiceException => {
        System.out.println("The email was not sent.")
        System.out.println("Service: " + ex.getServiceName + ", errorcode:" + ex.getErrorCode)
        MailStatus(to,false,Some(ex.getErrorCode))
      }
      case ex: Exception => {
        System.out.println("The email was not sent.")
        System.out.println("Error message: " + ex.getMessage)
        MailStatus(to,false,Some(ex.getMessage))
      }
    }
  }

}

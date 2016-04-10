package services

import models.MailStatus

import scala.concurrent.Future

/**
  * Created by unoedx on 10/04/16.
  */
trait MailService {
  def send(from:String,to:String,title:String, text:String, html:String):Future[MailStatus]
}

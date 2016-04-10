package models

/**
  * Created by unoedx on 09/04/16.
  */
case class BulkMail(
                     subject:String,
                     html:String,
                     text:String,
                     fromName:String,
                     fromEmail:String,
                     mails: Set[MailParams]
                   )

case class MailParams(email:String,params:Map[String,String]) {
  def paramsWithMail = params ++ Map("mail" -> email)
}

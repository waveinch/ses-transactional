package models

import play.api.libs.json.Json

/**
  * Created by mattia on 12/04/16.
  */
object Formatters {
  implicit val mailParamFormatter = Json.format[MailParams]
  implicit val bulkFormatter = Json.format[BulkMail]
  implicit val mailStatusFormatter = Json.format[MailStatus]
  implicit val bulkEmailAuthFormatter = Json.format[BulkMailAuth]
  implicit val sendMailAction = Json.format[SendMailAction]
}

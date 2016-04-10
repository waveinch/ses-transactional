package models

/**
  * Created by unoedx on 10/04/16.
  */
case class MailStatus(
                       email: String,
                       status: Boolean,
                       rejectReason: Option[String] = None
                     )
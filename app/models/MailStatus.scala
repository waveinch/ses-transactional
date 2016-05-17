package models

/**
  * Created by unoedx on 10/04/16.
  */
case class MailStatus(
                       email: String,
                       mailId: String,
                       status: Boolean,
                       rejectReason: Option[String] = None
                     )
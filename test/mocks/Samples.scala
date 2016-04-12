package mocks

import models.{BulkEmailAuth, BulkMail, MailParams, SendMailAction}

/**
  * Created by mattia on 12/04/16.
  */
object Samples {
  val bulkEmailAuth = BulkEmailAuth("xzc", "zxc", "zxc")
  val bulkMail = BulkMail(
    subject = "subject",
    html = "html",
    text = "text",
    fromName = "fromName",
    fromEmail = "from@email.ch",
    mails = Set(MailParams("a@a.ch",
      Map()
    ), MailParams("b@b.ch", Map()))
  )
  val sendmailAction = SendMailAction(
    bulkEmailAuth,
    bulkMail
  )
}

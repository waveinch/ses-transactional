package mocks

import models.{BulkMailAuth, BulkMail, MailParams, SendMailAction}

/**
  * Created by mattia on 12/04/16.
  */
object Samples {
  val bulkEmailAuth = BulkMailAuth("xzc", "zxc", "zxc")
  val bulkMail = BulkMail(
    subject = "subject",
    html = "html",
    text = "text",
    fromName = "fromName",
    fromEmail = "from@email.ch",
    mails = Seq(MailParams("a@a.ch",
      Map()
    ), MailParams("b@b.ch", Map()))
  )
  val sendmailAction = SendMailAction(
    bulkEmailAuth,
    bulkMail
  )


  val feedbackSuccess = """{"notificationType":"Delivery","mail":{"timestamp":"2016-06-13T07:48:30.240Z","source":"Hotel Du Lac - Amici del Ticino <prestay@dulac-amicidelticino.ch>","sourceArn":"arn:aws:ses:eu-west-1:185650108122:identity/dulac-amicidelticino.ch","sendingAccountId":"185650108122","messageId":"0102015548bbb9a0-22f153d7-ef73-406c-a103-fe535d66008c-000000","destination":["andrea@wavein.ch"]},"delivery":{"timestamp":"2016-06-13T07:48:31.016Z","processingTimeMillis":776,"recipients":["andrea@wavein.ch"],"smtpResponse":"250 2.0.0 OK 1465804110 j9si23135072wjt.128 - gsmtp","reportingMTA":"a7-31.smtp-out.eu-west-1.amazonses.com"}}"""
}

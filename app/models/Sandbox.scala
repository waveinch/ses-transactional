package models

/**
  * Created by unoedx on 06/05/16.
  */
object Sandbox {

    val successAddress = "success@simulator.amazonses.com"

    val success = Mail(
      from = "andrea@wavein.ch",
      to = successAddress,
      title = "success",
      text = "success",
      html = "success"
    )

    def bulkSuccess(qty:Int) = BulkMail(
      subject = "success",
      html = "success",
      text = "success",
      fromEmail = "andrea@wavein.ch",
      fromName = "ADT",
      mails = {for(i <- 1 until qty) yield { MailParams(successAddress,Map()) }}
    )
}

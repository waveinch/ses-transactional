package models

/**
  * Created by unoedx on 06/05/16.
  */
object Sandbox {

    val successAddress = "success@simulator.amazonses.com"
    val bounceAddress = "bounce@simulator.amazonses.com"
    val complaintAddress = "complaint@simulator.amazonses.com"
    val ootoAddress = "ooto@simulator.amazonses.com"
    val suppressionlistAddress = "suppressionlist@simulator.amazonses.com"

    val success = Mail(
      from = "andrea@wavein.ch",
      to = successAddress,
      title = "success",
      text = "success",
      html = "success"
    )

    def bulk(qty:Int,sendMail:String) = BulkMail(
      subject = "success",
      html = "success",
      text = "success",
      fromEmail = "andrea@wavein.ch",
      fromName = "ADT",
      mails = {for(i <- 1 until qty) yield { MailParams(sendMail,Map()) }}
    )

    def bulkSuccess(qty:Int) = bulk(qty,successAddress)
    def bulkBounce(qty:Int) = bulk(qty,bounceAddress)
    def bulkComplaint(qty:Int) = bulk(qty,complaintAddress)
    def bulkOoto(qty:Int) = bulk(qty,ootoAddress)
    def bulkSuppression(qty:Int) = bulk(qty,suppressionlistAddress)
}

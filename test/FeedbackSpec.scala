import mocks.Samples
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Json
import services.{Hash, Feedback}

/**
  * Created by unoedx on 13/06/16.
  */
class FeedbackSpec extends FlatSpec with Matchers {

  "Feedback" should "be calculated" in {

    val js = Json.parse(Samples.feedbackSuccess)
    (0 to 100).par.foreach { i =>
      println(i)
      val feedback = Feedback.fromMessage(js, "success")
      feedback.hashedMail.head shouldBe "0173cdc620137f730f114a4dd43d9ca1"
    }

  }

  "Hash" should "be calculated" in {
    (0 to 100).par.foreach { i =>
      println(i)
      val feedback = Hash.hashEmail("andrea@wavein.ch")
      feedback shouldBe "0173cdc620137f730f114a4dd43d9ca1"
    }
  }


}
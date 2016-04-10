import org.scalatest.{Matchers, FlatSpec}
import services.Template

/**
  * Created by unoedx on 10/04/16.
  */
class TemplateTest extends FlatSpec with Matchers {

  "Template" should "be rendered" in {
    val sampleTemplate = "pre {{test}} post"
    val sampleTemplate2 = "pre {{test   }} post"
    val sampleTemplate3 = "pre {{  test   }} post"
    val expectedResult = "pre rendered post"


    Template.render(sampleTemplate,Map("test"->"rendered")) shouldBe expectedResult
    Template.render(sampleTemplate2,Map("test"->"rendered")) shouldBe expectedResult
    Template.render(sampleTemplate3,Map("test"->"rendered")) shouldBe expectedResult

  }


}

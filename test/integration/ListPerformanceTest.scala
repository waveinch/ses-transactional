package integration

import org.joda.time.DateTime
import org.scalatestplus.play.OneAppPerSuite
import traits.TestBuilder

/**
  * Created by unoedx on 06/05/16.
  */
class ListPerformanceTest extends TestBuilder {

  var list:List[DateTime] = List()

  for(i <- 1 to 100000) {
     list = DateTime.now().minusSeconds(i) :: list
  }

  it should "evaluate speed of iterating over a list" in {
    val yesterday = DateTime.now().minusDays(1)
    list = DateTime.now() :: list.filter(_.isAfter(yesterday))
    assert(list.length < 90000)
  }



}

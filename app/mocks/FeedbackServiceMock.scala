package mocks


import services.{FeedbackService, Feedback}

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by unoedx on 22/08/16.
  */
class FeedbackServiceMock extends FeedbackService {




  override def delivery(feedback: Feedback): Future[Boolean] = Future{
    println(feedback)
    true
  }

  override def bounces(feedback: Feedback): Future[Boolean] =  Future{
    println(feedback)
    true
  }


}
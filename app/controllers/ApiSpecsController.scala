package controllers

import javax.inject.{Inject, Singleton}

import com.iheart.playSwagger.SwaggerSpecGenerator
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mattia on 30/05/16.
  */
@Singleton
class ApiSpecsController @Inject()()
                                  (implicit exec: ExecutionContext) extends Controller {
  implicit val cl = getClass.getClassLoader
  val domainPackage = "models"
  private lazy val generator = SwaggerSpecGenerator(domainPackage)

  def specs = Action.async { implicit request =>
    Future.fromTry(generator.generate("routes-swagger")).map(Ok(_))
  }
}

package services

import com.google.inject.Inject
import models.BulkEmailAuth
import models.Formatters._
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.mvc.Http.Status._

import scala.concurrent.Future

/**
  * Created by mattia on 12/04/16.
  */
class AuthServiceImpl @Inject()(
                                 configuration: Configuration,
                                 ws: WSClient) extends AuthService {
  val backendUrlKey = "adt.backend"
  val backendUrl = configuration.getString(backendUrlKey)
    .getOrElse(throw new Exception(s"Can't find $backendUrlKey key in config"))

  override def isAuthorized(auth: BulkEmailAuth): Future[Boolean] = ws.url(s"$backendUrl/adt/mail/send/valid")
    .post(Json.toJson(auth))
    .map(_.status == OK)
}

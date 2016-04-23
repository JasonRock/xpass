package controllers

import javax.inject._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao.SecretInfoDao
import domains.{ResponseStatus, SecretDetail}
import models._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(secretInfoDao: SecretInfoDao) extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    Ok(views.html.index("Your new application is ready.js.lee"))
  }

  def infos = Action.async {

    secretInfoDao.all().map(records => {
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records))
    })
  }

  def info(id: Int) = Action.async {
    secretInfoDao.queryById(id).map {
      case None => Ok(Json.obj("status" -> ResponseStatus.error(500, "No Results")))
      case record => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> record))
    }
  }

  def addSecretInfo() = Action.async(BodyParsers.parse.json) { request => {
    val SecretInfoResult = request.body.validate[SecretInfo]
    SecretInfoResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      secretInfo => {
        secretInfoDao.save(secretInfo).map(a => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> secretInfo)))
      }
    )
  }
  }

  def items = Action.async {
    secretInfoDao.allItems().map(records => {
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records))
    })
  }

  def item(id: Int) = Action.async {
    secretInfoDao.queryItemById(id).map {
      case None => Ok(Json.obj("status" -> ResponseStatus.error(500, "No Results")))
      case record => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> record))
    }
  }

  def classifies = Action.async {
    secretInfoDao.allClassifies().map(records => {
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records))
    })
  }

  def classify(id: Int) = Action.async {
    secretInfoDao.queryClassifyById(id).map {
      case None => Ok(Json.obj("status" -> ResponseStatus.error(500, "No Results")))
      case record => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> record))
    }
  }

  def details = Action.async {
    secretInfoDao.allDetails.map(records => {
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records))
    })
  }

  def detail(id: Int) = Action.async {
    secretInfoDao.queryDetailById(id).map {
      case null => Ok(Json.obj("status" -> ResponseStatus.error(500, "No Results")))
      case record => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> record))
    }
  }
}

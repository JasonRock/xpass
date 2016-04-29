package controllers

import javax.inject._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao.SecretInfoDao
import domains._
import models._
import org.apache.commons.codec.binary.Base64
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
      Ok(TransportResponse.info(Option(Json.toJson(records).toString())).toJson)
    })
  }

  def info(id: Int) = Action.async {
    secretInfoDao.queryById(id).map {
      case None => Ok(TransportResponse.error(500, "No Results").toJson)
      case record => Ok(TransportResponse.info(Option(Json.toJson(record).toString())).toJson)
    }
  }

  def addSecretInfo() = Action.async(BodyParsers.parse.json) { request => {
    val SecretInfoResult = request.body.validate[SecretInfo]
    SecretInfoResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      secretInfo => {
        secretInfoDao.save(secretInfo).map(a => {
          Ok(Json.toJson(TransportResponse.info(Option(Json.toJson(secretInfo).toString()))))
//          Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> secretInfo))
        })
      }
    )
  }
  }

  def items = Action.async {
    secretInfoDao.allItems().map(records => {
      val encrypt = TransportResponse.info(Option(Json.toJson(records).toString()))
      Ok(Json.toJson(encrypt))
    })
  }

  def item(id: Int) = Action.async {
    secretInfoDao.queryItemById(id).map {
      case None => Ok(Json.toJson(TransportResponse.error(500, "No Results")))
      case record => Ok(Json.toJson(TransportResponse.info(Option(Json.toJson(record).toString()))))
    }
  }

  def classifies = Action.async {
    secretInfoDao.allClassifies().map { records => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records)) }
  }

  def classify(id: Int) = Action.async {
    secretInfoDao.queryClassifyById(id).map {
      case None => Ok(Json.toJson(TransportResponse.error(500, "No Results")))
      case record => Ok(Json.toJson(TransportResponse.info(Option(Json.toJson(record).toString()))))
    }
  }

  def details = Action.async {
    secretInfoDao.allDetails.map { records => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> records)) }
  }

  def detail(id: Int) = Action.async {
    secretInfoDao.queryDetailById(id).map {
      case null => Ok(Json.toJson(TransportResponse.error(500, "No Results")))
      case record => Ok(Json.toJson(TransportResponse.info(Option(Json.toJson(record).toString()))))
    }
  }

  def addItem() = Action.async(BodyParsers.parse.json) { request => {
    val ItemInfoResult = request.body.validate[ItemInfo]
    ItemInfoResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      itemInfo => {
        secretInfoDao.saveItemInfo(itemInfo).map(_ => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> itemInfo)))
      }
    )
  }
  }

  def appendItem() = Action.async(BodyParsers.parse.json) { request => {
    val relationSecretItem = request.body.validate[RelationSecretItem]
    relationSecretItem.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
      },
      itemInfo => {
        secretInfoDao.appendItem(itemInfo)
        secretInfoDao.queryDetailById(itemInfo.secretId).map { i => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> i)) }
      }
    )
  }
  }

  import utils.crypto.{DES, AES}
  import utils.protocol.defaults._

  def encrypt() = Action(BodyParsers.parse.json) { request => {
    val encryptStr = Base64.encodeBase64String(AES.encrypt(request.body.toString(), "0123456789012345"))
    Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> encryptStr))
  }
  }

  def decrypt() = Action(BodyParsers.parse.json) { request => {
    val transportRequest = request.body.validate[TransportRequest]
    transportRequest.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> "aaa"))
      },
      info => {
        val parse = Json.parse(info.info.get)
        Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> parse))
      }
    )
  }

  }
}

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

  def infos = Action.async {

    secretInfoDao.all().map(records => {
      Ok(TransportResponse.info(records).toJson)
    })
  }

  def info(id: Int) = Action.async {
    secretInfoDao.queryById(id).map {
      case None => Ok(TransportResponse.error(500, "No Results").toJson)
      case record => Ok(TransportResponse.info(record).toJson)
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
          Ok(TransportResponse.info(secretInfo).toJson)
//          Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> secretInfo))
        })
      }
    )
  }
  }

  def items = Action.async {
    secretInfoDao.allItems().map(records => {
      Ok(TransportResponse.info(records).toJson)
    })
  }

  def item(id: Int) = Action.async {
    secretInfoDao.queryItemById(id).map {
      case None => Ok(TransportResponse.error(500, "No Results").toJson)
      case record => Ok(TransportResponse.info(record).toJson)
    }
  }

  def remainItems(secretId: Int) = Action.async {
    secretInfoDao.queryRemainItems(secretId).map {
      case record => Ok(TransportResponse.info(record).toJson)
    }
  }

  def classifies = Action.async {
    secretInfoDao.allClassifies().map { records => Ok(TransportResponse.info(records).toJson) }
  }

  def classify(id: Int) = Action.async {
    secretInfoDao.queryClassifyById(id).map {
      case None => Ok(TransportResponse.error(500, "No Results").toJson)
      case record => Ok(TransportResponse.info(record).toJson)
    }
  }

  def details = Action.async {
    secretInfoDao.allDetails.map { records => Ok(TransportResponse.info(records).toJson) }
  }

  def detail(id: Int) = Action.async {
    secretInfoDao.queryDetailById(id).map {
      case null => Ok(TransportResponse.error(500, "No Results").toJson)
      case record => Ok(TransportResponse.info(record).toJson)
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

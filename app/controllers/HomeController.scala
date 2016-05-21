package controllers

import javax.inject._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import dao.{ClassifyInfoDao, ItemInfoDao, SecretInfoDao, SecretItemDao}
import domains.{RelationSecretItem, _}
import models._
import org.apache.commons.codec.binary.Base64
import play.api.mvc._
import play.api.libs.json._
import utils.RSA
import utils.crypto.AES

import scala.concurrent.Future

/**
  * Home controller, provide major apis for client.
  *
  * @author js.ee
  */
@Singleton
class HomeController @Inject()(secretInfoDao: SecretInfoDao, itemInfoDao: ItemInfoDao,
                               classifyInfoDao: ClassifyInfoDao, secretItemDao: SecretItemDao) extends Controller {

  /**
    * Get all of the secret information.
    *
    * @return
    */
  def infos = Action.async {
    request => {
      val requestInfo: (String, String) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), null)
      }

      println("publicKey: " + requestInfo._1)
      secretInfoDao.all().map(records => {
        Ok(TransportResponse.info(records, requestInfo._1).toJson)
      })
    }
  }

  /**
    * Get the specified secret information.
    *
    * @return
    */
  def info(id: Int) = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val idTmp = Base64.encodeBase64String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString()))

      secretInfoDao.queryById(id).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, requestInfo._1).toJson)
      }
    }
  }

  /**
    * Add a new secret item.
    *
    * @return
    */
  def addSecretInfo() = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val secretInfo = Json.parse(new String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString())))
      val SecretInfoResult = secretInfo.validate[SecretInfo]
      SecretInfoResult.fold(
        errors => {
          Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
        },
        secretInfo => {
          secretInfoDao.save(secretInfo).map(a => {
            Ok(TransportResponse.info(secretInfo, requestInfo._1).toJson)
          })
        }
      )
    }
  }

  /**
    * Get all of the information items
    *
    * @return
    */
  def items = Action.async {
    request => {
      val requestInfo: (String, String) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), null)
      }

      itemInfoDao.allItems().map(records => {
        Ok(TransportResponse.info(records, requestInfo._1).toJson)
      })
    }
  }

  /**
    * Get the specified information items
    *
    * @return
    */
  def item(id: Int) = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val idTmp = Base64.encodeBase64String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString()))

      itemInfoDao.queryItemById(id).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, requestInfo._1).toJson)
      }
    }
  }

  /**
    * Get the information items that can be append to certain secret information
    *
    * @return
    */
  def remainItems(secretId: Int) = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val idTmp = Base64.encodeBase64String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString()))

      secretInfoDao.queryRemainItems(secretId).map {
        case record => Ok(TransportResponse.info(record, requestInfo._1).toJson)
      }
    }
  }

  /**
    * Get all the secret classifies.
    *
    * @return
    */
  def classifies = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), null)
      }
      classifyInfoDao.allClassifies().map { records => Ok(TransportResponse.info(records, requestInfo._1).toJson) }
    }
  }

  /**
    * Get the specified secret classifies.
    *
    * @return
    */
  def classify(id: Int) = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val idTmp = Base64.encodeBase64String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString()))

      classifyInfoDao.queryClassifyById(id).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, requestInfo._1).toJson)
      }
    }
  }

  /**
    * Get all of the secret information for detail.
    *
    * @return
    */
  def details = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), null)
      }
      secretInfoDao.allDetails.map { records => Ok(TransportResponse.info(records, requestInfo._1).toJson) }
    }
  }

  /**
    * Get the specified secret information for detail.
    *
    * @return
    */
  def detail(id: Int) = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val idTmp = Base64.encodeBase64String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString()))

      secretInfoDao.queryDetailById(id).map {
        case null => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, requestInfo._1).toJson)
      }
    }
  }

  /**
    * Add a new information item.
    *
    * @return
    */
  def addItem() = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val itemInfo = Json.parse(new String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString())))

      val ItemInfoResult = itemInfo.validate[ItemInfo]
      ItemInfoResult.fold(
        errors => {
          Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))))
        },
        itemInfo => {
          itemInfoDao.saveItemInfo(itemInfo).map(_ => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> itemInfo)))
        }
      )
    }
  }

  /**
    * Add a new item to specified secret information.
    * Update it if the item has been bound to the secret.
    *
    * @return
    */
  def appendItem() = Action.async {
    request => {
      val requestInfo: (String, JsValue) = request.body.asText.orNull match {
        case null => (null, null)
        case body: String =>
          val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body), "0123456789012345")))
          ((content \ "publicKey").get.toString(), (content \ "content").get)
      }

      val relationSecretItem = Json.parse(new String(RSA.decryptByPublicKey(requestInfo._1.getBytes, requestInfo._2.toString())))

      val relationSecretItemResult = relationSecretItem.validate[RelationSecretItem]
      relationSecretItemResult.fold(
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

}

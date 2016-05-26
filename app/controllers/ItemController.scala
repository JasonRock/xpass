package controllers

import javax.inject._

import action.BaseAction
import dao.{ClassifyInfoDao, ItemInfoDao, SecretInfoDao, SecretItemDao}
import domains.{RelationSecretItem, _}
import models._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

/**
  * Home controller, provide major apis for client.
  *
  * @author js.ee
  */
@Singleton
class ItemController @Inject()(secretInfoDao: SecretInfoDao, itemInfoDao: ItemInfoDao,
                               classifyInfoDao: ClassifyInfoDao, secretItemDao: SecretItemDao) extends Controller {

  /**
    * Get all of the information items
    *
    * @return
    */
  def items = BaseAction.async {
    request => {
      itemInfoDao.allItems().map(records => {
        Ok(TransportResponse.info(records, request.getPublicKey).toJson)
      })
    }
  }

  /**
    * Get the specified information items
    *
    * @return
    */
  def item() = BaseAction.async {
    request => {
      val id = (request.getInfo \ "id").get.toString()
      itemInfoDao.queryItemById(id.toInt).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, request.getPublicKey).toJson)
      }
    }
  }

  /**
    * Get the information items that can be append to certain secret information
    *
    * @return
    */
  def remainItems = BaseAction.async {
    request => {
      val secretId = request.getInfo.toString()
      secretInfoDao.queryRemainItems(secretId.toInt).map {
        case record => Ok(TransportResponse.info(record, request.getPublicKey).toJson)
      }
    }
  }

  /**
    * Add a new information item.
    *
    * @return
    */
  def addItem() = BaseAction.async {
    request => {
      val ItemInfoResult = request.getInfo.validate[ItemInfo]
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
  def appendItem() = BaseAction.async {
    request => {

      val relationSecretItemResult = request.getInfo.validate[RelationSecretItem]
      relationSecretItemResult.fold(
        errors => {
          Logger.error(JsError.toJson(errors).toString())
          Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> "what???")))
        },
        itemInfo => {
          secretInfoDao.appendItem(itemInfo)
          secretInfoDao.queryDetailById(itemInfo.secretId).map { i => Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> i)) }
        }
      )
    }
  }
}

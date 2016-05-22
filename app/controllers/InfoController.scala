package controllers

import javax.inject._

import action.BaseAction
import dao.SecretInfoDao
import domains._
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
class InfoController @Inject()(secretInfoDao: SecretInfoDao) extends Controller {

  /**
    * Get all of the secret information.
    *
    * @return
    */
  def infos = BaseAction.async {
    request => {
      secretInfoDao.all().map(records => {
        Ok(TransportResponse.info(records, request.getPublicKey).toJson)
      })
    }
  }

  /**
    * Get the specified secret information.
    *
    * @return
    */
  def info = BaseAction.async {
    request => {
      val id = request.getInfo.toString()
      secretInfoDao.queryById(id.toInt).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, request.getPublicKey).toJson)
      }
    }
  }

  /**
    * Add a new secret item.
    *
    * @return
    */
  def addSecretInfo() = BaseAction.async {
    request => {
      val SecretInfoResult = request.getInfo.validate[SecretInfo]
      SecretInfoResult.fold(
        errors => {
          Logger.error(JsError.toJson(errors).toString())
          Future.successful(BadRequest(Json.obj("status" -> "KO", "message" -> "are you kidding me???")))
        },
        secretInfo => {
          secretInfoDao.save(secretInfo).map(a => {
            Ok(TransportResponse.info(secretInfo, request.getPublicKey).toJson)
          })
        }
      )
    }
  }

  /**
    * Get all of the secret information for detail.
    *
    * @return
    */
  def details = BaseAction.async {
    request => {
      secretInfoDao.allDetails.map {
        records => Ok(TransportResponse.info(records, request.getPublicKey).toJson)
      }
    }
  }

  /**
    * Get the specified secret information for detail.
    *
    * @return
    */
  def detail(id: Int) = BaseAction.async {
    request => {
      val id = request.getInfo.toString()
      secretInfoDao.queryDetailById(id.toInt).map {
        case null => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, request.getPublicKey).toJson)
      }
    }
  }
}

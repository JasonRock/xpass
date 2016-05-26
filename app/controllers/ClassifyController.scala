package controllers

import javax.inject._

import action.BaseAction
import dao.ClassifyInfoDao
import domains.TransportResponse
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

/**
  * Home controller, provide major apis for client.
  *
  * @author js.ee
  */
@Singleton
class ClassifyController @Inject()(classifyInfoDao: ClassifyInfoDao) extends Controller {

  /**
    * Get all the secret classifies.
    *
    * @return
    */
  def classifies = BaseAction.async {
    request => {
      classifyInfoDao.allClassifies().map {
        records => Ok(TransportResponse.info(records, request.getPublicKey).toJson)
      }
    }
  }

  /**
    * Get the specified secret classifies.
    *
    * @return
    */
  def classify = BaseAction.async {
    request => {
      val id = (request.getInfo \ "id").get.toString()
      classifyInfoDao.queryClassifyById(id.toInt).map {
        case None => Ok(TransportResponse.error(500, "No Results").toJson)
        case record => Ok(TransportResponse.info(record, request.getPublicKey).toJson)
      }
    }
  }

}

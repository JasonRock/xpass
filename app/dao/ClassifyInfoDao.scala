package dao

import javax.inject.Inject

import models.{ClassifyInfo}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by js.lee on 5/5/16.
  */
class ClassifyInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DaoHelper {

  import driver.api._

  /** t_classify_info */
  def allClassifies(): Future[Seq[ClassifyInfo]] = db.run(classifyInfos.result)

  def queryClassifyById(id: Int): Future[Option[ClassifyInfo]] = db.run(classifyInfos.filter(_.id === id).result.headOption)

  def saveClassifyInfo(classify: ClassifyInfo): Future[Int] = db.run(classifyInfos += classify)


}

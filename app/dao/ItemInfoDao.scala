package dao

import javax.inject.Inject

import models.ItemInfo
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by js.lee on 5/5/16.
  */
class ItemInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DaoHelper {

  import driver.api._

  def allItems(): Future[Seq[ItemInfo]] = {
    db.run(itemInfos.result)
  }

  def queryItemById(id: Int): Future[Option[ItemInfo]] = db.run(itemInfos.filter(_.id === id).result.headOption)

  def queryRemainItems(secretId: Int): Future[Seq[ItemInfo]] = {
    db.run(secretItems.filter(_.secretId === secretId).result).flatMap(result => {
      db.run(itemInfos.filterNot(_.id inSet result.map(_.itemId.getOrElse(0))).result)
    })
  }

  def saveItemInfo(itemInfo: ItemInfo): Future[Int] = db.run(itemInfos += itemInfo)

}

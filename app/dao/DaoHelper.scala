package dao

import models.{ClassifyInfo, ItemInfo, SecretInfo, SecretItem}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by js.lee on 5/5/16.
  */
trait DaoHelper extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /** Mapping of columns to the row object */
  class SecretInfos(tag: Tag) extends Table[SecretInfo](tag, "t_secret_info") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def title = column[Option[String]]("title")

    def classify = column[Option[Int]]("classify")

    def description = column[Option[String]]("description")

    def createTime = column[Option[String]]("create_time")

    def updateTime = column[Option[String]]("update_time")

    def * = (id, title, classify, description, createTime, updateTime) <>((SecretInfo.apply _).tupled, SecretInfo.unapply)
  }

  class ItemInfos(tag: Tag) extends Table[ItemInfo](tag, "t_item_info") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def itemName = column[Option[String]]("item_name")

    def itemDesc = column[Option[String]]("item_desc")

    def securityLevel = column[Option[Int]]("security_level")

    def * = (id, itemName, itemDesc, securityLevel) <>((ItemInfo.apply _).tupled, ItemInfo.unapply)
  }

  class ClassifyInfos(tag: Tag) extends Table[ClassifyInfo](tag, "t_classify_info") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def name = column[Option[String]]("name")

    def description = column[Option[String]]("description")

    def icon = column[Option[String]]("icon")

    def * = (id, name, description, icon) <>((ClassifyInfo.apply _).tupled, ClassifyInfo.unapply)
  }

  class SecretItems(tag: Tag) extends Table[SecretItem](tag, "r_secret_item") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def secretId = column[Option[Int]]("secret_id")

    def itemId = column[Option[Int]]("item_id")

    def itemContent = column[Option[String]]("item_content")

    def * = (id, secretId, itemId, itemContent) <>((SecretItem.apply _).tupled, SecretItem.unapply)
  }

  val secretInfos = TableQuery[SecretInfos]
  val itemInfos = TableQuery[ItemInfos]
  val classifyInfos = TableQuery[ClassifyInfos]
  val secretItems = TableQuery[SecretItems]

}

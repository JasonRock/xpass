package dao

import javax.inject.Inject

import domains.{RelationSecretItem, ResponseStatus, SecretDetail}
import domains.SecretDetail._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import models._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json

import scala.collection.Traversable

/**
  * Created by js.lee on 4/20/16.
  */
class SecretInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

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

  /** Base query for the table */
  object secretInfos extends TableQuery(new SecretInfos(_))

  object itemInfos extends TableQuery(new ItemInfos(_))

  object classifyInfos extends TableQuery(new ClassifyInfos(_))

  object secretItems extends TableQuery(new SecretItems(_))

  /** t_secret_info */
  def all(): Future[Seq[SecretInfo]] = db.run(secretInfos.result)

  def queryById(id: Int): Future[Option[SecretInfo]] = db.run(secretInfos.filter(_.id === id).result.headOption)

  def save(secretInfo: SecretInfo): Future[Int] = db.run(secretInfos += secretInfo)

  /** t_item_info */
  def allItems(): Future[Seq[ItemInfo]] = db.run(itemInfos.result)

  def queryItemById(id: Int): Future[Option[ItemInfo]] = db.run(itemInfos.filter(_.id === id).result.headOption)

  def queryRemainItems(secretId: Int): Future[Seq[ItemInfo]] = {
    db.run(secretItems.filter(_.secretId === secretId).result).flatMap(result => {
      db.run(itemInfos.filterNot(_.id inSet result.map(_.itemId.getOrElse(0))).result)
    })
  }

  def saveItemInfo(itemInfo: ItemInfo): Future[Int] = db.run(itemInfos += itemInfo)

  /** t_classify_info */
  def allClassifies(): Future[Seq[ClassifyInfo]] = db.run(classifyInfos.result)

  def queryClassifyById(id: Int): Future[Option[ClassifyInfo]] = db.run(classifyInfos.filter(_.id === id).result.headOption)

  def saveClassifyInfo(classify: ClassifyInfo): Future[Int] = db.run(classifyInfos += classify)

  /** t_secret_item */
  def allSecretItmes(): Future[Seq[SecretItem]] = db.run(secretItems.result)

  def querySecretItemById(id: Int): Future[Option[SecretItem]] = db.run(secretItems.filter(_.id === id).result.headOption)

  def saveOrUpdateSecretItem(secretItem: SecretItem): Future[Int] = {
    val secretId = secretItem.secretId
    val itemId = secretItem.itemId

    db.run(secretItems.filter(_.secretId === secretId).filter(_.itemId === itemId).result.headOption).flatMap {
      case None => {
        // save
        db.run(secretItems += secretItem)
      }
      case result => {
        // update
        val q = for {
          c <- secretItems if c.id === result.get.id
        } yield c.itemContent
        db.run(q.update(secretItem.itemContent))
      }
    }
  }

  def allDetails: Future[Seq[SecretDetail]] = {
    val q = for {
      si <- secretItems
      s <- secretInfos if s.id === si.id
      i <- itemInfos if i.id === si.id
    } yield (s.id, s.title, i.itemName, i.itemDesc, i.securityLevel, si.itemContent)
    db.run(q.result).map(i => {
      i.map(j => {
        SecretDetail.apply _ tupled j
      })
    })
  }

  def queryDetailById(id: Int): Future[Seq[SecretDetail]] = {
    val q = for {
      s <- secretInfos
      i <- itemInfos
      si <- secretItems if s.id === si.secretId && i.id === si.itemId && s.id === id
    } yield (s.id, s.title, i.itemName, i.itemDesc, i.securityLevel, si.itemContent)
    db.run(q.result) map {
      case j => j.map(jj => {
        SecretDetail.apply _ tupled jj
      })
    }
  }

  def appendItem(relationSecretItem: RelationSecretItem): Future[Int] = {
    val secretId = relationSecretItem.secretId
    db.run(secretInfos.filter(_.id === secretId).result.headOption).map {
      case None => -1
      case secretInfo => {
        val itemId = relationSecretItem.itemId
        db.run(itemInfos.filter(_.id === itemId).result.headOption).map {
          case None => -1
          case itemInfo => {
            saveOrUpdateSecretItem(SecretItem(Option.empty[Int], Option(secretId), Option(itemId), Option(relationSecretItem.itemContent)))
          }
        }
        1
      }
    }
  }

}

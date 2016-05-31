package dao

import java.util.Calendar
import javax.inject.Inject

import domains.{RelationSecretItem, SecretDetail}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import models._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by js.lee on 4/20/16.
  */
class SecretInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DaoHelper {

  import driver.api._

  def all(): Future[Seq[SecretInfo]] = db.run(secretInfos.result)

  def queryById(id: Int): Future[Option[SecretInfo]] = db.run(secretInfos.filter(_.id === id).result.headOption)

  def save(secretInfo: SecretInfo): Future[Int] = db.run(secretInfos += secretInfo)

  def queryRemainItems(secretId: Int): Future[Seq[ItemInfo]] = {
    db.run(secretItems.filter(_.secretId === secretId).result).flatMap(result => {
      db.run(itemInfos.filterNot(_.id inSet result.map(_.itemId.getOrElse(0))).result)
    })
  }

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
    } yield (s.id, s.title, i.id, i.itemName, i.itemDesc, i.securityLevel, si.itemContent)
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
    } yield (s.id, s.title, i.id, i.itemName, i.itemDesc, i.securityLevel, si.itemContent)
    db.run(q.result) map {
      case j => j.map(jj => {
        SecretDetail.apply _ tupled jj
      })
    }
  }

  def appendItem(relationSecretItem: RelationSecretItem): Future[Int] = {
    val secretId = relationSecretItem.secretId
    db.run(secretInfos.filter(_.id === secretId).result.headOption).flatMap {
      case None => Future(-1)
      case secretInfo => {
        val itemId = relationSecretItem.itemId
        db.run(itemInfos.filter(_.id === itemId).result.headOption).flatMap {
          case None => Future(-1)
          case itemInfo => {
            saveOrUpdateSecretItem(SecretItem(Option.empty[Int], Option(secretId), Option(itemId), Option(relationSecretItem.itemContent)))
          }
        }
      }
    }
  }

}

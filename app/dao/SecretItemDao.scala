package dao

import javax.inject.Inject

import models.SecretItem
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

/**
  * Created by js.lee on 5/5/16.
  */
class SecretItemDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DaoHelper {

  import driver.api._

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

}

package models

import play.api.libs.json.{Json, _}
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

case class SecretInfo(id: Option[Int], title: Option[String], classify: Option[Int], description: Option[String], createTime: Option[String], updateTime: Option[String])

object SecretInfo {

  implicit val userReads: Reads[SecretInfo] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "title").readNullable[String] ~
      (__ \ "classify").readNullable[Int] ~
      (__ \ "description").readNullable[String] ~
      (__ \ "createTime").readNullable[String] ~
      (__ \ "updateTime").readNullable[String]
    ) (SecretInfo.apply _)

  implicit lazy val userWrites: Writes[SecretInfo] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "title").write[Option[String]] ~
      (__ \ "classify").write[Option[Int]] ~
      (__ \ "description").write[Option[String]] ~
      (__ \ "createTime").write[Option[String]] ~
      (__ \ "updateTime").write[Option[String]]
    ) (unlift(SecretInfo.unapply))
}

case class ItemInfo(id: Option[Int], itemName: Option[String], itemDesc: Option[String], securityLevel: Option[Int])

object ItemInfo {
  implicit val itemInfoReads: Reads[ItemInfo] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "itemName").readNullable[String] ~
      (__ \ "itemDesc").readNullable[String] ~
      (__ \ "securityLevel").readNullable[Int]
    ) (ItemInfo.apply _)

  implicit lazy val itemInfoWrites: Writes[ItemInfo] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "itemName").write[Option[String]] ~
      (__ \ "itemDesc").write[Option[String]] ~
      (__ \ "securityLevel").write[Option[Int]]
    ) (unlift(ItemInfo.unapply))
}

case class ClassifyInfo(id: Option[Int], name: Option[String], description: Option[String], icon: Option[String])

object ClassifyInfo {
  implicit val classifyInfoReads: Reads[ClassifyInfo] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "name").readNullable[String] ~
      (__ \ "description").readNullable[String] ~
      (__ \ "icon").readNullable[String]
    ) (ClassifyInfo.apply _)

  implicit lazy val classifyInfoWrites: Writes[ClassifyInfo] = (
    (__ \ "id").write[Option[Int]] ~
      (__ \ "name").write[Option[String]] ~
      (__ \ "description").write[Option[String]] ~
      (__ \ "icon").write[Option[String]]
    ) (unlift(ClassifyInfo.unapply))
}

case class SecretItem(id: Option[Int], secretId: Option[Int], itemId: Option[Int], itemContent: Option[String])

object SecretItem {

  var readId: Reads[Option[Int]] = (__ \ "id").readNullable[Int]
  var readSecretId: Reads[Option[Int]] = (__ \ "secretId").readNullable[Int]
  var readItemId: Reads[Option[Int]] = (__ \ "itemId").readNullable[Int]
  var readItemContent: Reads[Option[String]] = (__ \ "itemContent").readNullable[String]
  implicit val secretItemReads: Reads[SecretItem] = (
    readId ~
      readSecretId ~
      readItemId ~
      readItemContent
    ) (SecretItem.apply _)

  var writeId: OWrites[Option[Int]] = (__ \ "id").writeNullable[Int]
  var writeSecretId: OWrites[Option[Int]] = (__ \ "secretId").writeNullable[Int]
  var writeItemId: OWrites[Option[Int]] = (__ \ "itemId").writeNullable[Int]
  var writeItemContent: OWrites[Option[String]] = (__ \ "itemContent").writeNullable[String]
  implicit val secretItemWrites: Writes[SecretItem] = (
    writeId ~
      writeSecretId ~
      writeItemId ~
      writeItemContent
    ) (unlift(SecretItem.unapply))
}
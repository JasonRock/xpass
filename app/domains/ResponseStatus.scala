package domains

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

/**
  * Created by js.lee on 4/20/16.
  */
case class SecretDetail(id: Option[Int], title: Option[String], itemId: Option[Int], itemName: Option[String], itemDesc: Option[String], securityLevel: Option[Int], itemContent: Option[String])

object SecretDetail {
  implicit val reads: Reads[SecretDetail] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "title").readNullable[String] ~
      (__ \ "itemId").readNullable[Int] ~
      (__ \ "itemName").readNullable[String] ~
      (__ \ "itemDesc").readNullable[String] ~
      (__ \ "securityLevel").readNullable[Int] ~
      (__ \ "itemContent").readNullable[String]
    ) (SecretDetail.apply _)

  implicit lazy val writes: Writes[SecretDetail] = (
    (__ \ "id").writeNullable[Int] ~
      (__ \ "title").writeNullable[String] ~
      (__ \ "itemId").writeNullable[Int] ~
      (__ \ "itemName").writeNullable[String] ~
      (__ \ "itemDesc").writeNullable[String] ~
      (__ \ "securityLevel").writeNullable[Int] ~
      (__ \ "itemContent").writeNullable[String]
    ) (unlift(SecretDetail.unapply))
}

case class RelationSecretItem(secretId: Int, itemId: Int, itemContent: String)

object RelationSecretItem {
  implicit val reads: Reads[RelationSecretItem] = (
    (__ \ "secretId").read[Int] ~
      (__ \ "itemId").read[Int] ~
      (__ \ "itemContent").read[String]
    ) (RelationSecretItem.apply _)

  implicit lazy val writes: Writes[RelationSecretItem] = (
    (__ \ "secretId").write[Int] ~
      (__ \ "itemId").write[Int] ~
      (__ \ "itemContent").write[String]
    ) (unlift(RelationSecretItem.unapply))
}
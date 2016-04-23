package domains

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

/**
  * Created by js.lee on 4/20/16.
  */
case class ResponseStatus(code: Int, message: String)

object ResponseStatus {
  implicit val reads: Reads[ResponseStatus] = (
    (__ \ "code").read[Int] ~
      (__ \ "message").read[String]
    ) (ResponseStatus.apply _)

  implicit val writes: Writes[ResponseStatus] = (
    (__ \ "code").write[Int] ~
      (__ \ "message").write[String]
    ) (unlift(ResponseStatus.unapply))

  def success() = Json.toJson(ResponseStatus(200, "success"))

  def error(code: Int, message: String) = Json.toJson(ResponseStatus(code, message))
}


case class SecretDetail(id: Option[Int], title: Option[String], itemName: Option[String], itemDesc: Option[String], securityLevel: Option[Int], itemContent: Option[String])

object SecretDetail {
  implicit val reads: Reads[SecretDetail] = (
    (__ \ "id").readNullable[Int] ~
      (__ \ "title").readNullable[String] ~
      (__ \ "itemName").readNullable[String] ~
      (__ \ "itemDesc").readNullable[String] ~
      (__ \ "securityLevel").readNullable[Int] ~
      (__ \ "itemContent").readNullable[String]
    ) (SecretDetail.apply _)

  implicit lazy val writes: Writes[SecretDetail] = (
    (__ \ "id").writeNullable[Int] ~
      (__ \ "title").writeNullable[String] ~
      (__ \ "itemName").writeNullable[String] ~
      (__ \ "itemDesc").writeNullable[String] ~
      (__ \ "securityLevel").writeNullable[Int] ~
      (__ \ "itemContent").writeNullable[String]
    ) (unlift(SecretDetail.unapply))
}
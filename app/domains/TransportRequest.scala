package domains

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, _}

/**
  * Created by js.lee on 4/29/16.
  */
class TransportRequest(val publicKey: String, val info: Option[String]) {
  override def toString = Json.toJson(this).toString()
}

object TransportRequest {

  implicit val reads: Reads[TransportRequest] = (
    (__ \ "publicKey").read[String] ~
      (__ \ "info").readNullable[String]
    ) (TransportRequest.apply _)

  implicit val writes: Writes[TransportRequest] = (
    (__ \ "publicKey").write[String] ~
      (__ \ "info").writeNullable[String]
    ) (unlift(TransportRequest.unapply))

  def apply(publicKey: String, info: Option[String]): TransportRequest = {
    info match {
      case error if error.isEmpty => new TransportRequest(publicKey, info)
      case _ => new TransportRequest(publicKey, info)
    }
  }

  def unapply(arg: TransportRequest): Option[(String, Option[String])] = Some(arg.publicKey, arg.info)
}

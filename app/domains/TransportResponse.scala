package domains

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Writes, _}
import org.apache.commons.codec.binary.Base64
import utils.crypto.{DES, AES}

/**
  * Created by js.lee on 4/29/16.
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

class TransportResponse(val status: ResponseStatus, val info: Option[String]) {
  def toJson: JsValue = Json.toJson(this)
}

object TransportResponse {

  implicit val reads: Reads[TransportResponse] = (
    (__ \ "status").read[ResponseStatus] ~
      (__ \ "info").readNullable[String]
    ) (TransportResponse.apply _)

  implicit val writes: Writes[TransportResponse] = (
    (__ \ "status").write[ResponseStatus] ~
      (__ \ "info").writeNullable[String]
    ) (unlift(TransportResponse.unapply))


  def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)

  def apply(status: ResponseStatus, info: Option[String]): TransportResponse = {
    info match {
      case error if error == Option.empty => new TransportResponse(status, Option.empty)
      case _ => {
        val encrypted: String = encodeBase64(AES.encrypt(info.get.getBytes, "0123456789012345"))
        new TransportResponse(status, Option(encrypted))
      }
    }

  }

  def unapply(arg: TransportResponse): Option[(ResponseStatus, Option[String])] = Option(arg.status, arg.info)

  def info(info: String) = {
    info match {
      case i if i == null => TransportResponse(ResponseStatus(200, "success"), Option.empty)
      case _ => TransportResponse(ResponseStatus(200, "success"), Option(info))
    }
  }

  def info[T](o: T)(implicit tjs: Writes[T]): TransportResponse = {
    info(Json.toJson(o).toString())
  }

  def error(code: Int, message: String) = {
    TransportResponse(ResponseStatus(code, message), Option.empty)
  }
}

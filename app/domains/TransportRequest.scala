package domains

import org.apache.commons.codec.binary.Base64
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, Writes, _}
import utils.crypto.{AES, DES}

/**
  * Created by js.lee on 4/29/16.
  */
class TransportRequest(val magic: Int, val info: Option[String])

object TransportRequest {

  implicit val reads: Reads[TransportRequest] = (
    (__ \ "magic").read[Int] ~
      (__ \ "info").readNullable[String]
    ) (TransportRequest.apply _)

  implicit val writes: Writes[TransportRequest] = (
    (__ \ "magic").write[Int] ~
      (__ \ "info").writeNullable[String]
    ) (unlift(TransportRequest.unapply))

  def apply(magic: Int, info: Option[String]): TransportRequest = {
    info match {
      case error if error.isEmpty => new TransportRequest(magic, info)
      case _ => {
        val decrypt = new String(AES.decrypt(Base64.decodeBase64(info.get), "0123456789012345"))
        new TransportRequest(magic, Option(decrypt))
      }
    }
  }

  def unapply(arg: TransportRequest): Option[(Int, Option[String])] = Some(arg.magic, arg.info)
}

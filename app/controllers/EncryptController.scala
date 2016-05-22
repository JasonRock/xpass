package controllers

import javax.inject._

import domains._
import org.apache.commons.codec.binary.Base64
import play.api.libs.json._
import play.api.mvc._

/**
  * Encrypt and Decrypt data for test.
  *
  * @author js.ee
  */
@Singleton
class EncryptController @Inject()() extends Controller {

  import utils.crypto.AES
  import utils.protocol.defaults._

  /**
    * Encrypt transport data with AES.
    *
    * @return
    */
  def encrypt() = Action(BodyParsers.parse.json) {
    request => {
      val encryptStr = Base64.encodeBase64String(AES.encrypt(request.body.toString(), AES.AES_KEY))
      Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> encryptStr))
    }
  }

  /**
    * Decrypt transport data with AES.
    *
    * @return
    */
  def decrypt() = Action(BodyParsers.parse.json) {
    request => {
      val transportRequest = request.body.validate[TransportRequest]
      transportRequest.fold(
        errors => {
          BadRequest(Json.obj("status" -> "KO", "message" -> "aaa"))
        },
        info => {
          val parse = Json.parse(info.info.get)
          Ok(Json.obj("status" -> ResponseStatus.success(), "info" -> parse))
        }
      )
    }
  }
}

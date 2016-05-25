package action

import domains.TransportRequest
import org.apache.commons.codec.binary.Base64
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import request.BaseRequest
import utils.RSA
import utils.crypto.AES

import scala.concurrent.Future

/**
  * Created by js.lee on 5/22/16.
  */
object BaseAction extends ActionBuilder[BaseRequest] {

  def transform[A](request: Request[A]) = {
    val transportRequest: TransportRequest = request.body match {
      case null => null
      case body: AnyContentAsText =>

        val content: JsValue = Json.parse(new String(AES.decrypt(Base64.decodeBase64(body.asText.orNull), AES.AES_KEY)))

        val publicKey: String = (content \ "publicKey").get.toString()
        val info: String = content \ "info" toOption match {
          case None => null
          case a =>  new String(RSA.decryptByPublicKey(Base64.decodeBase64(a.get.toString()), publicKey))
        }
        TransportRequest(publicKey, Option(info))
    }
    new BaseRequest(transportRequest, request)
  }

  def invokeBlock[A](request: Request[A], block: (BaseRequest[A]) => Future[Result]): Future[Result] = {
    val res = transform(request)
    Logger.debug("decrypted request body: \n%s".format(res.transportRequest))
    block(res)
  }

}

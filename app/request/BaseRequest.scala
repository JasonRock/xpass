package request

import domains.TransportRequest
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.mvc.WrappedRequest

/**
  * Created by js.lee on 5/22/16.
  */
class BaseRequest[A](val transportRequest: TransportRequest, request: Request[A]) extends WrappedRequest[A](request) {

  def getPublicKey = this.transportRequest.publicKey

  def getInfo: JsValue = this.transportRequest match {
    case null => null
    case _ => Json.toJson(this.transportRequest.info)
  }
}

package request

import domains.TransportRequest
import play.api.mvc._
import play.api.mvc.WrappedRequest

/**
  * Created by js.lee on 5/22/16.
  */
class BaseRequest[A](val transportRequest: TransportRequest, request: Request[A]) extends WrappedRequest[A](request)

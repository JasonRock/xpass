package handler

import play.api.http.HttpRequestHandler
import javax.inject.Inject

import play.api.Logger
import play.api.mvc._
import play.api.routing.Router

/**
  * Created by js.lee on 5/22/16.
  */
class SimpleHttpRequestHandler @Inject()(router: Router) extends HttpRequestHandler {
  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {
    Logger.info("handler start...")
    router.routes.lift(request) match {
      case Some(handler) => {

        (request, handler)
      }
      case None => {
        println("handler: 404")
        (request, Action(Results.NotFound))
      }
    }
  }
}

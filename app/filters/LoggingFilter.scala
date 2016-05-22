package filters

import javax.inject._

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._
import play.api.routing.Router.Tags

import scala.concurrent.{ExecutionContext, Future}

/**
  * For debug log
  *
  * @param mat  This object is needed to handle streaming of requests
  *             and responses.
  * @param exec This class is needed to execute code asynchronously.
  *             It is used below by the `map` method.
  */
@Singleton
class LoggingFilter @Inject()(
                               implicit override val mat: Materializer,
                               exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    // Run the next filter in the chain. This will call other filters
    // and eventually call the action. Take the result and modify it
    // by adding a new header.

    val startTime = System.currentTimeMillis
    nextFilter(requestHeader).map { result =>

      val action = requestHeader.tags(Tags.RouteController) +
        "." + requestHeader.tags(Tags.RouteActionMethod)
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      Logger.debug(s"${action} took ${requestTime}ms and returned ${result.header.status}")
      result
    }
  }

}

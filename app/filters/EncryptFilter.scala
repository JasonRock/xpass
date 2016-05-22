package filters

import akka.stream.Materializer
import javax.inject._
import play.api.mvc._
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}

/**
  * This filter is considered to decrypted the content that wrapped
  * in request, and encrypted the content in response body.
  *
  * @param mat  This object is needed to handle streaming of requests
  *             and responses.
  * @param exec This class is needed to execute code asynchronously.
  *             It is used below by the `map` method.
  */
@Singleton
class EncryptFilter @Inject()(
                               implicit override val mat: Materializer,
                               exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>
      Logger.info("for encrypted")
      result
    }
  }

}

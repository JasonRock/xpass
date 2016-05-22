package filters

import javax.inject._

import play.api._
import play.api.http.HttpFilters

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 *
 * Play will automatically use filters from any class called
 * `Filters` that is placed the root package. You can load filters
 * from a different class by adding a `play.http.filters` setting to
 * the `application.conf` configuration file.
 *
 * @param env Basic environment settings for the current application.
 * @param encryptFilter A demonstration filter that adds a header to
 * @param loggingFilter A demonstration filter that for debug log
 * each response.
 */
@Singleton
class Filters @Inject()(
  env: Environment,
  encryptFilter: EncryptFilter,
  loggingFilter: LoggingFilter) extends HttpFilters {

  override val filters = {
    // Use the example filter if we're running development mode. If
    // we're running in production or test mode then don't use any
    // filters at all.
//    if (env.mode == Mode.Dev) Seq(exampleFilter) else Seq.empty
    Seq(loggingFilter, encryptFilter)
  }

}

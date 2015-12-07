package be.iqit.vertx.glue.rest

import be.iqit.vertx.glue.convert.AbstractConverter
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import rx.Observable

/**
 * Created by dvanroeyen on 07/12/15.
 */
class SessionConverter extends AbstractConverter<RoutingContext, Session> {

    @Override
    Observable<Session> convert(RoutingContext routingContext, Class<Session> clazz) {
        return Observable.just(routingContext.session())
    }
}

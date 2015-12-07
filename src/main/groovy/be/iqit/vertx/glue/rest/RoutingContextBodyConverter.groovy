package be.iqit.vertx.glue.rest

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.DelegatingConverter
import be.iqit.vertx.sample.user.domain.UserFilter
import io.vertx.core.MultiMap
import io.vertx.ext.web.RoutingContext
import rx.Observable

/**
 * Created by dvanroeyen on 07/12/15.
 */
class RoutingContextBodyConverter<O> extends DelegatingConverter<RoutingContext, O> {

    RoutingContextBodyConverter(Converter converter) {
        super(converter)
    }

    @Override
    Observable<O> convert(RoutingContext routingContext, Class<O> clazz) {
        return converter.convert(routingContext.getBodyAsString(), clazz)
    }

}

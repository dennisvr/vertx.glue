package be.iqit.vertx.sample.user

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.DelegatingConverter
import be.iqit.vertx.sample.user.domain.UserFilter
import io.vertx.core.MultiMap
import io.vertx.ext.web.RoutingContext
import rx.Observable

/**
 * Created by dvanroeyen on 07/12/15.
 */
class UserFilterConverter extends DelegatingConverter<RoutingContext, UserFilter> {

    UserFilterConverter(Converter converter) {
        super(converter)
    }

    @Override
    Observable<UserFilter> convert(RoutingContext object, Class<UserFilter> clazz) {
        return converter.convert(toMap(object.request().params()), clazz)
    }

    private Map<String, Object> toMap(MultiMap multiMap) {
        def Map<String, Object> map = [:]
        multiMap.each { map.put(it.key, it.value)}
        return map
    }
}

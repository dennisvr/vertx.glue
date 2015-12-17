/**
 * Copyright 2015 Dennis Van Roeyen, iQit, BVBA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
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

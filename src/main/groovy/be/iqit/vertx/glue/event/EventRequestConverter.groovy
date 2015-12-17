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
package be.iqit.vertx.glue.event

import be.iqit.vertx.glue.convert.AbstractConverter
import be.iqit.vertx.glue.convert.Converter
import rx.Observable

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventRequestConverter extends AbstractConverter<EventRequest,List<Object>> {


    Converter converter

    public EventRequestConverter(Converter converter) {
        this.converter = converter
    }

    @Override
    Observable<List<Object>> convert(EventRequest object, Class<List<Object>> clazz) {
        return Observable.from(object.parameters).flatMap({ EventParameter parameter ->
            converter.convert(parameter.value, parameter.clazz)
        }).toList()
    }
}

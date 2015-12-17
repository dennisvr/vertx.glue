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
package be.iqit.vertx.glue.convert

import com.fasterxml.jackson.databind.node.ArrayNode
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
abstract class AbstractBaseConverter<I,O> extends AbstractConverter<I,O> {

    @Override
    Observable<O> convert(I object, Class<O> clazz) {
        try {
            if (object == null) {
                return Observable.just(null)
            }
            if (object instanceof Collection && !String.isAssignableFrom(clazz)) {
                return Observable.from(object)
                        .flatMap({ item -> convert(item, clazz) })
                        .toList()
            }
            if(object instanceof ArrayNode && !String.isAssignableFrom(clazz)) {
                return Observable.from(object)
                        .flatMap({ item -> convert(item, clazz) })
                        .toList()
            }
            if (clazz && clazz.isAssignableFrom(object.getClass())) {
                return Observable.just(object)
            }
            return doConvert(object, clazz)
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not convert ${object.getClass()} to ${clazz}")
        }
    }

    abstract Observable<O> doConvert(I object, Class<O> clazz);
}

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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class ObjectMapperConverter<I,O>  extends AbstractBaseConverter<I,O> {

    ObjectMapper objectMapper

    public ObjectMapperConverter() {
        this(new ObjectMapper())
    }

    public ObjectMapperConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    @Override
    Observable<O> doConvert(I object, Class<O> clazz) {
        if (object instanceof String) {
            return Observable.just(objectMapper.readValue(object, clazz))
        }
        if (JsonNode.isAssignableFrom(clazz)) {
            return Observable.just(objectMapper.valueToTree(object))
        }
        if (String.isAssignableFrom(clazz)) {
            return Observable.just(objectMapper.valueToTree(object).toString())
        }
        return Observable.just(objectMapper.convertValue(object, clazz))
    }
}

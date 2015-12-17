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

import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class FactoryConverter<I,O> extends AbstractBaseConverter<I,O> {

    Map<ConverterKey, Converter> map = [:]
    Converter defaultConverter

    FactoryConverter withDefaultConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter
        return this
    }

    FactoryConverter withConverter(Class from, Class to, Converter converter) {
        map.put(getKey(from, to), converter)
        return this
    }

    @Override
    Observable<O> doConvert(I object, Class<O> clazz) {
        Converter converter = findConverter(object?.getClass(), clazz)
        if(!converter) {
            throw new IllegalArgumentException("No converter for ${object.getClass()} to ${clazz}")
        }
        return converter.convert(object, clazz)
    }

    public Converter findConverter(Class from, Class to) {
        Converter converter =  map.get(getKey(from, to))
        if(!converter) {
            converter = defaultConverter
        }
        return converter
    }

    private ConverterKey getKey(Class from, Class to) {
        return new ConverterKey(from, to)
    }

    private class ConverterKey {
        Class from
        Class to

        public ConverterKey(Class from, Class to) {
            this.from = from
            this.to = to
        }

        @Override
        boolean equals(Object obj) {
            if(!obj instanceof ConverterKey) {
                return false
            }

            ConverterKey that = obj

            //Compatible from and equal to
            return that.from.isAssignableFrom(this.from)&&that.to.equals(this.to)
        }

        @Override
        int hashCode() {
            //only check to
            return to.hashCode()
        }
    }
}

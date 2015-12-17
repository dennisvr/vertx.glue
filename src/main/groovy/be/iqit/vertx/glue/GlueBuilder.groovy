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
package be.iqit.vertx.glue

import be.iqit.vertx.glue.event.EventConsumer
import be.iqit.vertx.glue.event.EventSender
import be.iqit.vertx.glue.event.EventVerticle

/**
 * Created by dvanroeyen on 08/12/15.
 */
class GlueBuilder {

    EventSender eventSender
    EventConsumer eventConsumer

    public GlueBuilder(EventSender eventSender, EventConsumer eventConsumer) {
        this.eventSender = eventSender
        this.eventConsumer = eventConsumer
    }

    public <E> EventVerticle<E> createVerticle(Class<E> interfaceClass, E instance) {
        return new EventVerticle(eventConsumer, interfaceClass, instance)
    }

    public <E> E createRemote(Class<E> interfaceClass) {
        def map = [:]

        interfaceClass.methods.each() { method ->
            map."${method.name}" = { Object[] args->
                eventSender.send(interfaceClass, method.name, resolveClass(method.annotatedReturnType.type), *args)
            }
        }

        return map.asType(interfaceClass)
    }

    private Class resolveClass(def type) {
        try {
            if (type.actualTypeArguments) {
                return resolveClass(type.actualTypeArguments[0])
            }
        } catch (MissingPropertyException e) {
            // leaf, expected behaviour
        }
        return type
    }
}

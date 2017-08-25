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

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.FactoryConverter
import be.iqit.vertx.glue.convert.StringToThrowableConverter
import be.iqit.vertx.glue.convert.ThrowableToStringConverter
import be.iqit.vertx.glue.rest.AsyncResultObservableHandler
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import rx.Observable


/**
 * Created by dvanroeyen on 03/12/15.
 */
class VertxEventSender implements EventSender {

    FactoryConverter converter
    Vertx vertx

    public VertxEventSender(Vertx vertx, Converter converter) {
        this.vertx = vertx
        this.converter = new FactoryConverter().withDefaultConverter(converter)
    }

    public <O> Observable<O> send(Class interfaceClass, String method, Class<O> clazz, Object...params) {
        return converter.convert(new EventRequest(params), JsonNode)
                .map({message ->
            if(message instanceof Collection) {
                message = new ArrayNode().addAll(message)
            }
            return message.toString()
        }).flatMap({ message ->
            AsyncResultObservableHandler<Message> handler = new AsyncResultObservableHandler<>(this.converter)
            String address = "${interfaceClass.name}.${method}"
            vertx.eventBus().send(address, message , handler);
            return handler.asObservable().flatMap({ m ->
                converter.convert(converter.convert(m.body(), JsonNode), clazz)
            })
        })
    }
}

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
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import org.codehaus.groovy.runtime.MethodClosure
import rx.Observer

/**
 * Created by dvanroeyen on 03/12/15.
 */
class VertxEventConsumer implements EventConsumer{

    FactoryConverter converter
    Vertx vertx

    public VertxEventConsumer(Vertx vertx, Converter converter) {
        this.vertx = vertx
        this.converter = new FactoryConverter().withDefaultConverter(converter)
        this.converter.withConverter(EventRequest,List,new EventRequestConverter(this.converter))
    }

    public <E> void consume(Class<E> interfaceClass, MethodClosure method) {
        String address = "${interfaceClass.name}.${method.method}"
        vertx.eventBus().consumer(address, new Handler<Message>() {
            @Override
            void handle(Message event) {
                def body = event.body()
                converter.convert(converter.convert(body , EventRequest), List).flatMap({ eventParameters ->
                    method.call(eventParameters.toArray())

                }).subscribe(new Observer() {
                    @Override
                    void onCompleted() {

                    }

                    @Override
                    void onError(Throwable e) {
                        e.printStackTrace()
                        //event.reply(e)
                        converter.convert(e, String).subscribe(new Observer<String>() {
                            @Override
                            void onCompleted() {

                            }

                            @Override
                            void onError(Throwable t) {
                                e.printStackTrace()
                                event.fail(502, e.message)
                            }

                            @Override
                            void onNext(String s) {
                                event.fail(500,s)
                            }
                        })

                    }

                    @Override
                    void onNext(Object object) {
                        converter.convert(object, String).subscribe(new Observer<String>() {
                            @Override
                            void onCompleted() {

                            }

                            @Override
                            void onError(Throwable e) {
                                e.printStackTrace()
                                event.fail(501, e.message)
                            }

                            @Override
                            void onNext(String s) {
                                event.reply(s.toString())
                            }
                        })
                    }
                })

            }
        })
    }


}

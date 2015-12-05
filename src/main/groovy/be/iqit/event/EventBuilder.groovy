package be.iqit.event

import be.iqit.convert.Converter
import be.iqit.convert.FactoryConverter
import be.iqit.rest.AsyncResultObservableHandler
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import org.codehaus.groovy.runtime.MethodClosure
import rx.Observable
import rx.Observer

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventBuilder {

    FactoryConverter converter
    Vertx vertx
    Class interfaceClass


    public EventBuilder(Class interfaceClass, Converter converter, Vertx vertx) {
        this.interfaceClass = interfaceClass
        this.converter = new FactoryConverter().withDefaultConverter(converter)
        this.converter.withConverter(EventRequest,List,new EventRequestConverter(this.converter))
        this.vertx = vertx
    }

    public <O> Observable<O> send(MethodClosure method, Class<O> clazz, Object...params) {
        return converter.convert(new EventRequest(params), JsonNode)
                .map({message ->
                    if(message instanceof Collection) {
                        message = new ArrayNode().addAll(message)
                    }
                    return message.toString()
                })
                .flatMap({ message ->
            AsyncResultObservableHandler<Message> handler = new AsyncResultObservableHandler<>()
            String address = "${interfaceClass.name}.${method.method}"
            vertx.eventBus().send(address, message , handler);
            return handler.asObservable().flatMap({ m ->
               converter.convert(converter.convert(m.body(), JsonNode), clazz)
            })
        })
    }

    public void consume(MethodClosure method) {
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
                        event.fail(500, e.message)
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

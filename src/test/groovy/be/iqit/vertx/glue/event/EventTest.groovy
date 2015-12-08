package be.iqit.vertx.glue.event

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.ObjectMapperConverter
import io.vertx.core.Vertx
import rx.functions.Func1
import spock.lang.Specification

/**
 * Created by dvanroeyen on 04/12/15.
 */
class EventTest extends Specification {

    def "can send and consume"() {
        given:
        Vertx vertx = Vertx.vertx()
        Converter converter = new ObjectMapperConverter()
        VertxEventSender eventSender = new VertxEventSender(vertx, converter)
        VertxEventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)
        EventVerticle eventVerticle = new EventVerticle(eventConsumer, TestService, new DefaultTestService())
        vertx.deployVerticle(eventVerticle)

        when:
        Integer result = eventSender.send(TestService, "foo", Integer, "32", 10).toBlocking().first()

        then:
        result == 42

    }

    def "can handle exceptions"() {
        given:
        Vertx vertx = Vertx.vertx()
        Converter converter = new ObjectMapperConverter()
        VertxEventSender eventSender = new VertxEventSender(vertx, converter)
        VertxEventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)
        EventVerticle eventVerticle = new EventVerticle(eventConsumer, TestService, new DefaultTestService())
        vertx.deployVerticle(eventVerticle)

        when:
        String result = eventSender.send(TestService, "throwError", Integer).onErrorReturn(new Func1<Throwable, String>() {
            @Override
            String call(Throwable throwable) {
                return throwable.message
            }
        }).toBlocking().first()

        then:
        result == "test"

    }

}

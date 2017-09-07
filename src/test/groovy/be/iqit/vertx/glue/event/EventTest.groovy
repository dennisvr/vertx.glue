package be.iqit.vertx.glue.event

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.FactoryConverter
import be.iqit.vertx.glue.convert.ObjectMapperConverter
import be.iqit.vertx.glue.convert.StringToThrowableConverter
import be.iqit.vertx.glue.convert.ThrowableToStringConverter
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
        Converter rootConverter = new ObjectMapperConverter()
        FactoryConverter converter = new FactoryConverter()
                .withDefaultConverter(rootConverter)
                .withConverter(Throwable, String, new ThrowableToStringConverter(rootConverter))
                .withConverter(String, Throwable, new StringToThrowableConverter(rootConverter))
        VertxEventSender eventSender = new VertxEventSender(vertx, converter)
        VertxEventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)
        EventVerticle eventVerticle = new EventVerticle(eventConsumer, TestService, new DefaultTestService())
        vertx.deployVerticle(eventVerticle)

        when:
        eventSender.send(TestService, "throwError", Integer).toBlocking().first()

        then:
        final IllegalArgumentException exception = thrown()
        exception.message == 'test'

    }

}

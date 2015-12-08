package be.iqit.vertx.glue.event

import io.vertx.core.AbstractVerticle
import org.codehaus.groovy.runtime.MethodClosure

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventVerticle<E> extends AbstractVerticle {

    Class<E> interfaceClass
    E instance
    VertxEventConsumer eventConsumer

    public EventVerticle(VertxEventConsumer eventConsumer, Class<E> interfaceClass, E instance) {
        this.eventConsumer = eventConsumer
        this.interfaceClass = interfaceClass
        this.instance = instance
    }

    @Override
    void start() throws Exception {
        super.start()
        interfaceClass.methods.each {
            eventConsumer.consume(interfaceClass, new MethodClosure(instance, it.name))
        }
    }

}

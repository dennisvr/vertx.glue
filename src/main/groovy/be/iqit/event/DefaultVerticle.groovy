package be.iqit.event

import be.iqit.convert.Converter
import io.vertx.core.AbstractVerticle
import org.codehaus.groovy.runtime.MethodClosure

/**
 * Created by dvanroeyen on 03/12/15.
 */
class DefaultVerticle<E> extends AbstractVerticle {

    Class<E> interfaceClass
    E instance
    Converter converter


    public DefaultVerticle(Class<E> interfaceClass, E instance, Converter converter) {
        this.interfaceClass = interfaceClass
        this.instance = instance
        this.converter = converter
    }

    @Override
    void start() throws Exception {
        super.start()
        EventBuilder eventBuilder = new EventBuilder(interfaceClass, converter, vertx)
        interfaceClass.methods.each {
            eventBuilder.consume(new MethodClosure(instance, it.name))
        }
    }

}

package be.iqit.vertx.glue.event;

import org.codehaus.groovy.runtime.MethodClosure;

/**
 * Created by dvanroeyen on 08/12/15.
 */
public interface EventConsumer {

    <E> void consume(Class<E> interfaceClass, MethodClosure method);

}

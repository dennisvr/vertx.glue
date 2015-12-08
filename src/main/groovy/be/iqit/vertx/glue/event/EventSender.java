package be.iqit.vertx.glue.event;

import rx.Observable;

/**
 * Created by dvanroeyen on 08/12/15.
 */
public interface EventSender {

    <O> Observable<O> send(Class interfaceClass, String method, Class<O> clazz, Object...params);
}

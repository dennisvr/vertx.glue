package be.iqit.vertx.glue.rest;

import groovy.transform.TypeChecked;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by dvanroeyen on 02/12/15.
 */
@TypeChecked
public class AsyncResultObservableHandler<E> implements Handler<AsyncResult<E>> {

    private final Observable<E> observable;
    private Subscriber<? super E> subscriber;

    public AsyncResultObservableHandler() {
        this.observable = Observable.create( new Observable.OnSubscribe<E>() {
            @Override
            public void call(Subscriber<? super E> subscriber) {
                AsyncResultObservableHandler.this.subscriber = subscriber;
            }
        });
    }

    public Observable<E> asObservable() {
        return this.observable;
    }

    @Override
    public void handle(AsyncResult<E> event) {
        if(event.failed()) {
            this.subscriber.onError(event.cause());
        } else {
            this.subscriber.onNext(event.result());
        }
        this.subscriber.onCompleted();
    }
}

package be.iqit.vertx.glue.rest;

import io.vertx.core.Handler;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by dvanroeyen on 30/11/15.
 */
public class ObservableHandler<E> implements Handler<E> {

    private final Observable<E> observable;
    private Subscriber<? super E> subscriber;

    public ObservableHandler() {
       this.observable = Observable.create( new Observable.OnSubscribe<E>() {
           @Override
           public void call(Subscriber<? super E> subscriber) {
               ObservableHandler.this.subscriber = subscriber;
           }
        });
    }

    public Observable<E> asObservable() {
        return this.observable;
    }

    @Override
    public void handle(E event) {
        this.subscriber.onNext(event);
        this.subscriber.onCompleted();
    }
}

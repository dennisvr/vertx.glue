package be.iqit.vertx.glue.mongo;

import com.mongodb.async.SingleResultCallback;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by dvanroeyen on 08/12/15.
 */
class ObservableSingleResultCallback<E> implements SingleResultCallback<E> {

    private final Observable<E> observable;
    private Subscriber<? super E> subscriber;

    public ObservableSingleResultCallback() {
        this.observable = Observable.create( new Observable.OnSubscribe<E>() {
            @Override
            public void call(Subscriber<? super E> subscriber) {
                ObservableSingleResultCallback.this.subscriber = subscriber;
            }
        });
    }

    @Override
    public void onResult(E result, Throwable t) {
        if(t!=null) {
            this.subscriber.onError(t);
        } else {
            this.subscriber.onNext(result);
        }
        this.subscriber.onCompleted();
    }

    public Observable<E> asObservable() {
        return this.observable;
    }


}

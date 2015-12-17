/**
 * Copyright 2015 Dennis Van Roeyen, iQit, BVBA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package be.iqit.vertx.glue.mongo;

/**
 * Created by dvanroeyen on 30/11/15.
 */

import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.Observables;
import com.mongodb.async.client.Observer;
import com.mongodb.async.client.Subscription;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by dvanroeyen on 30/11/15.
 */
public class MongoUtil {

    public static <E> Observable<E> asObservable(FindIterable<E> iterable) {
        return asObservable(Observables.observe(iterable));
    }

    public static <E> Observable<E> asObservable(final com.mongodb.async.client.Observable<E> observable) {
        return Observable.create( new Observable.OnSubscribe<E>() {
            @Override
            public void call(final Subscriber<? super E> subscriber) {
                observable.subscribe(new Observer<E>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                        subscriber.onStart();
                    }

                    @Override
                    public void onNext(E e) {
                        subscriber.onNext(e);
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onCompleted();
                    }
                });

            }
        });
    }
}

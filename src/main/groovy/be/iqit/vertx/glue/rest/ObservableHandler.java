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

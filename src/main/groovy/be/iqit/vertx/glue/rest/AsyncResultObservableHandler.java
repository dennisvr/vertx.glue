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

import be.iqit.vertx.glue.convert.Converter;
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
    private final Converter converter;
    private Subscriber<? super E> subscriber;

    public AsyncResultObservableHandler(Converter converter) {
        this.converter = converter;
        this.observable = Observable.create(subscriber -> AsyncResultObservableHandler.this.subscriber = subscriber);
    }

    public Observable<E> asObservable() {
        return this.observable;
    }

    @Override
    public void handle(AsyncResult<E> event) {
        if(event.failed()) {
            Throwable throwable = (Throwable) this.converter.convert(event.cause().getMessage(), Throwable.class).toBlocking().first();
            this.subscriber.onError(throwable);
        } else {
            this.subscriber.onNext(event.result());
        }
        this.subscriber.onCompleted();
    }
}

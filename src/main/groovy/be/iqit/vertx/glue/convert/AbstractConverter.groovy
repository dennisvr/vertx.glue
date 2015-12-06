package be.iqit.vertx.glue.convert

import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
abstract class AbstractConverter<I,O> implements Converter<I,O> {

    @Override
    Observable<O> convert(Observable<I> observable, Class<O> clazz) {
        return observable.flatMap({ object ->
            this.convert(object, clazz);
        })
    }

}

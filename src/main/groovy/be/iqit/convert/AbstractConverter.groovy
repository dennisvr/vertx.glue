package be.iqit.convert

import com.fasterxml.jackson.databind.node.ArrayNode
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

    @Override
    Observable<O> convert(I object, Class<O> clazz) {
        try {
            if (object == null) {
                return Observable.just(null)
            }
            if (object instanceof Collection && !String.isAssignableFrom(clazz)) {
                return Observable.from(object)
                        .flatMap({ item -> convert(item, clazz) })
                        .toList()
            }
            if(object instanceof ArrayNode && !String.isAssignableFrom(clazz)) {
                return Observable.from(object)
                        .flatMap({ item -> convert(item, clazz) })
                        .toList()
            }
            if (clazz && clazz.isAssignableFrom(object.getClass())) {
                return Observable.just(object)
            }
            return doConvert(object, clazz)
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not convert ${object.getClass()} to ${clazz}")
        }
    }

    abstract Observable<O> doConvert(I object, Class<O> clazz);
}

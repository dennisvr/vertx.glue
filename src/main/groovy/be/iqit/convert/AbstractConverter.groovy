package be.iqit.convert

import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
abstract class AbstractConverter implements Converter {

    @Override
    def <I, O> Observable<O> convert(Observable<I> observable, Class<O> clazz) {
        return observable.flatMap({ object ->
            this.convert(object, clazz);
        })
    }

    @Override
    <I,O> Observable<O> convert(I object, Class<O> clazz) {
        try {
            if (object == null) {
                return Observable.just(null)
            }
            if (object instanceof Collection) {
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

    abstract <I,O> Observable<O> doConvert(I object, Class<O> clazz);
}

package be.iqit.convert;

import rx.Observable;

/**
 * Created by dvanroeyen on 02/12/15.
 */
public interface Converter<I,O> {

    Observable<O> convert(Observable<I> object, Class<O> clazz);

    Observable<O> convert(I object, Class<O> clazz);
}

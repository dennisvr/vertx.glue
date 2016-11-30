package be.iqit.vertx.glue.convert

import rx.Observable

/**
 * Created by dvanroeyen on 13/03/16.
 */
class StringToThrowableConverter  extends DelegatingConverter<String, Throwable> {

    StringToThrowableConverter(Converter converter) {
        super(converter)
    }

    @Override
    Observable<String> convert(String value, Class<Throwable> clazz) {
        converter.convert(value, ClassDTO).flatMap({ ClassDTO classDTO ->
            return converter.convert(classDTO.value, classDTO.clazz)
        })
    }

}

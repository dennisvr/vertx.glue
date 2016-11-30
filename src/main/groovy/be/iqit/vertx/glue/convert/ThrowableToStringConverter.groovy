package be.iqit.vertx.glue.convert

import rx.Observable

/**
 * Created by dvanroeyen on 13/03/16.
 */
class ThrowableToStringConverter extends DelegatingConverter<Throwable, String> {

    ThrowableToStringConverter(Converter converter) {
        super(converter)
    }

    @Override
    Observable<String> convert(Throwable throwable, Class<String> clazz) {
        converter.convert(throwable, String).flatMap({ String value ->
            ClassDTO classDTO = new ClassDTO(clazz: throwable.class, value: value)
            return converter.convert(classDTO, String)
        })

    }

}

package be.iqit.vertx.glue.event

import be.iqit.vertx.glue.convert.AbstractConverter
import be.iqit.vertx.glue.convert.Converter
import rx.Observable

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventRequestConverter extends AbstractConverter<EventRequest,List<Object>> {


    Converter converter

    public EventRequestConverter(Converter converter) {
        this.converter = converter
    }

    @Override
    Observable<List<Object>> convert(EventRequest object, Class<List<Object>> clazz) {
        return Observable.from(object.parameters).flatMap({ EventParameter parameter ->
            converter.convert(parameter.value, parameter.clazz)
        }).toList()
    }
}

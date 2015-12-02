package be.iqit.convert

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class ObjectMapperConverter implements Converter {

    ObjectMapper objectMapper

    public ObjectMapperConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

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
                        .flatMap({ item ->
                    convert(item, clazz)
                })
                        .toList()
            }
            if (clazz && clazz.isAssignableFrom(object.getClass())) {
                return Observable.just(object)
            }
            if (JsonNode.isAssignableFrom(clazz)) {
                return Observable.just(objectMapper.valueToTree(object))
            }
            if (String.isAssignableFrom(clazz)) {
                return Observable.just(objectMapper.valueToTree(object).toString())
            }
            if (object instanceof String) {
                return Observable.just(objectMapper.readValue(object, clazz))
            }
            return Observable.just(objectMapper.convertValue(object, clazz))
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not convert ${object.getClass()} to ${clazz}")
        }
    }
}

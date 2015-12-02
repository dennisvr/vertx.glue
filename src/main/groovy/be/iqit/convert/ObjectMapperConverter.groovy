package be.iqit.convert

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class ObjectMapperConverter extends AbstractConverter {

    ObjectMapper objectMapper

    public ObjectMapperConverter() {
        this(new ObjectMapper())
    }

    public ObjectMapperConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    @Override
    <I,O> Observable<O> doConvert(I object, Class<O> clazz) {
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
    }
}

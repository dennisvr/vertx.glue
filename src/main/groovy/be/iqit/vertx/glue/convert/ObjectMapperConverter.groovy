package be.iqit.vertx.glue.convert

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class ObjectMapperConverter<I,O>  extends AbstractBaseConverter<I,O> {

    ObjectMapper objectMapper

    public ObjectMapperConverter() {
        this(new ObjectMapper())
    }

    public ObjectMapperConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    @Override
    Observable<O> doConvert(I object, Class<O> clazz) {
        if (object instanceof String) {
            return Observable.just(objectMapper.readValue(object, clazz))
        }
        if (JsonNode.isAssignableFrom(clazz)) {
            return Observable.just(objectMapper.valueToTree(object))
        }
        if (String.isAssignableFrom(clazz)) {
            return Observable.just(objectMapper.valueToTree(object).toString())
        }
        return Observable.just(objectMapper.convertValue(object, clazz))
    }
}

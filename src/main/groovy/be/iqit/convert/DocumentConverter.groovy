package be.iqit.convert

import org.bson.Document
import rx.Observable

/**
 * Created by dvanroeyen on 04/12/15.
 */
class DocumentConverter extends AbstractConverter<Document, String> {

    @Override
    Observable<String> convert(Document document, Class<String> clazz) {
        return Observable.just(document.toJson())
    }

}

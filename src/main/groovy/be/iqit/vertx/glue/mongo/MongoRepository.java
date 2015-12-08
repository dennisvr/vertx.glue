package be.iqit.vertx.glue.mongo;

import org.bson.Document;
import org.bson.conversions.Bson;
import rx.Observable;

/**
 * Created by dvanroeyen on 06/12/15.
 */
public interface MongoRepository {

    Observable<Document> find();

    Observable<Document> find(Bson filter);

    Observable<Void> save(Document document);
}

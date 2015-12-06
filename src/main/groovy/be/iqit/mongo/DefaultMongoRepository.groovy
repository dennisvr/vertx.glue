package be.iqit.mongo

import com.mongodb.async.client.MongoCollection

import org.bson.Document
import org.bson.conversions.Bson
import rx.Observable

/**
 * Created by dvanroeyen on 06/12/15.
 */
class DefaultMongoRepository implements  MongoRepository {

    MongoCollection<Document> collection

    public DefaultMongoRepository(MongoCollection collection ) {
        this.collection = collection
    }

    public Observable<Document> find() {
        MongoUtil.asObservable(collection.find())
    }

    public Observable<Document> find(Bson filter) {
        MongoUtil.asObservable(collection.find(filter))
    }

}
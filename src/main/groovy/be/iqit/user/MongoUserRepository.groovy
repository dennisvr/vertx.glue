package be.iqit.user

import be.iqit.convert.Converter
import be.iqit.convert.ObjectMapperConverter
import be.iqit.mongo.MongoUtil
import com.mongodb.async.client.MongoCollection
import com.mongodb.async.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class MongoUserRepository implements UserRepository {

    MongoCollection<Document> collection
    Converter converter = new ObjectMapperConverter()

    public MongoUserRepository(MongoDatabase database) {
        this.collection = database.getCollection("users")
    }

    @Override
    Observable<User> getUser(String id) {
        return converter.convert(MongoUtil.asObservable(collection.find(Filters.eq('id', id))), User)
    }

    @Override
    Observable<User> getUsers() {
        return MongoUtil.asObservable(collection.find())
    }
}

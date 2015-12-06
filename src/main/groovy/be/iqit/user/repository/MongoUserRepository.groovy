package be.iqit.user.repository

import be.iqit.convert.Converter
import be.iqit.convert.DocumentConverter
import be.iqit.convert.FactoryConverter
import be.iqit.mongo.DefaultMongoRepository
import be.iqit.mongo.MongoRepository
import be.iqit.user.domain.User
import com.mongodb.client.model.Filters
import org.bson.Document
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class MongoUserRepository implements UserRepository {

    MongoRepository repository
    FactoryConverter converter

    public MongoUserRepository(MongoRepository repository, Converter converter ) {
        this.repository = repository
        this.converter = new FactoryConverter()
                .withDefaultConverter(converter)
              //  .withConverter(Document, String, new DocumentConverter())
    }

    @Override
    Observable<User> getUser(String id) {
        return converter.convert(repository.find(Filters.eq('id', id)), User)
    }

    @Override
    Observable<User> getUsers() {
        return converter.convert(repository.find(), User)
    }
}

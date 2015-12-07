package be.iqit.vertx.sample.user.repository

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.FactoryConverter
import be.iqit.vertx.glue.mongo.MongoRepository
import be.iqit.vertx.sample.domain.User
import com.mongodb.client.model.Filters
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

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return converter.convert(repository.find(
                Filters.and(
                    Filters.eq('email',email),
                    Filters.eq('password',password)
                )
        ), User)
    }
}

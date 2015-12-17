/**
 * Copyright 2015 Dennis Van Roeyen, iQit, BVBA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package be.iqit.vertx.glue.demo.repository

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.FactoryConverter
import be.iqit.vertx.glue.mongo.MongoRepository
import be.iqit.vertx.glue.demo.domain.User
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

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return converter.convert(repository.find(
                Filters.and(
                    Filters.eq('email',email),
                    Filters.eq('password',password)
                )
        ), User)
    }

    @Override
    Observable<User> saveUser(User user) {
        converter.convert(user, Document).flatMap({ document ->
            return repository.save(document)
        }).map({
            return user
        })

    }
}

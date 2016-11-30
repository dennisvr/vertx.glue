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
package be.iqit.vertx.glue.common.repository

import be.iqit.vertx.glue.mongo.Repository
import be.iqit.vertx.glue.common.domain.User
import com.mongodb.client.model.Filters
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class MongoUserRepository implements UserRepository {

    Repository<User> repository

    public MongoUserRepository(Repository<User> repository ) {
        this.repository = repository
    }

    @Override
    Observable<User> getUser(String id) {
        return repository.find(Filters.eq('id', id))
    }

    @Override
    Observable<User> getUsers() {
        return repository.find()
    }

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return repository.find(
                Filters.and(
                    Filters.eq('email',email),
                    Filters.eq('password',password)
                )
        ).defaultIfEmpty(null)
    }

    @Override
    Observable<User> findWithEmail(String email) {
        return repository.find(
                Filters.and(
                        Filters.eq('email',email),
                )
        ).defaultIfEmpty(null)
    }

    @Override
    Observable<User> saveUser(User user) {
            return repository.save(user)
                    .map({
                return user
            })

    }
}

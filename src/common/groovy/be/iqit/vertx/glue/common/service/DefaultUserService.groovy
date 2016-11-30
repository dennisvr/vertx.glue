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
package be.iqit.vertx.glue.common.service

import be.iqit.vertx.glue.common.repository.UserRepository
import be.iqit.vertx.glue.common.domain.User
import be.iqit.vertx.glue.paging.Page
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class DefaultUserService implements UserService {

    UserRepository userRepository

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    Observable<User> getUser(String id) {
        return userRepository.getUser(id).defaultIfEmpty(null)
                .flatMap({ x ->
                    if(!x) {
                        return Observable.error(new IllegalArgumentException("Unknown user:${id}"))
                    }
                    return Observable.just(x)
                })
    }

    @Override
    Observable<User> saveUser(User user) {
        if(user.id==null) {
            user.id = UUID.randomUUID().toString()
        }
        if(user.login==null) {
            user.login = user.email
        }
        return userRepository.saveUser(user)
    }

    @Override
    Observable<List<User>> getUsers() {
        return userRepository.getUsers().toList()
    }

    @Override
    Observable<Page<User>> getUserPage() {
        return userRepository.getUsers().toList().map({ users ->
            return new Page(users, 0, 0, users.size())
        })
    }

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return this.userRepository.getUserWithEmailAndPassword(email, password)
    }

    @Override
    Observable<User> findWithEmail(String email) {
        return this.userRepository.findWithEmail(email)
    }
}

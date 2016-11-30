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

import be.iqit.vertx.glue.common.domain.User
import be.iqit.vertx.glue.paging.Page
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
interface UserService {

    Observable<User> getUser(String id)

    Observable<User> saveUser(User user)

    Observable<List<User>> getUsers()

    Observable<Page<User>> getUserPage()

    Observable<User> getUserWithEmailAndPassword(String email, String password)

    Observable<User> findWithEmail(String email)
}
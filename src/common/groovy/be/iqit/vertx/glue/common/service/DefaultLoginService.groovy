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
import io.vertx.ext.web.Session
import rx.Observable

/**
 * Created by dvanroeyen on 07/12/15.
 */
class DefaultLoginService implements LoginService {

    UserService userService

    public DefaultLoginService(UserService userService) {
        this.userService = userService
    }

    @Override
    Observable<User> login(Session session, String email, String password) {
        return userService.getUserWithEmailAndPassword(email, password).doOnNext({ user ->
            session.data().user = user
        })
    }

    @Override
    Observable<Boolean> logout(Session session) {
        return Observable.just(session).map({ s ->
            s.destroy()
        }).map({
            return true
        })
    }
}

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
package be.iqit.vertx.glue.demo.rest

import be.iqit.vertx.glue.rest.UnauthorizedException
import io.vertx.ext.web.Session
import rx.Observable

/**
 * Created by dvanroeyen on 08/12/15.
 */
class Authorization {

    static Closure<Observable<Boolean>> IS_USER = { Session session ->
        if(session.data().user == null) {
            Observable.error(new UnauthorizedException("Not logged in"))
        }
        Observable.just(true)
    }


    static Closure<Observable<Boolean>> IS_ADMIN= { Session session ->
        if(session.data()?.user?.admin) {
            Observable.error(new UnauthorizedException("Not admin"))
        }
        Observable.just(true)
    }
}

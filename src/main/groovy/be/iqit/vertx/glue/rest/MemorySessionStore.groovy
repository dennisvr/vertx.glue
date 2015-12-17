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
package be.iqit.vertx.glue.rest

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.Session
import io.vertx.ext.web.sstore.SessionStore

/**
 * Created by dvanroeyen on 06/12/15.
 */
class MemorySessionStore implements SessionStore {
    @Override
    Session createSession(long timeout) {
        return null
    }

    @Override
    void get(String id, Handler<AsyncResult<Session>> resultHandler) {

    }

    @Override
    void delete(String id, Handler<AsyncResult<Boolean>> resultHandler) {

    }

    @Override
    void put(Session session, Handler<AsyncResult<Boolean>> resultHandler) {

    }

    @Override
    void clear(Handler<AsyncResult<Boolean>> resultHandler) {

    }

    @Override
    void size(Handler<AsyncResult<Integer>> resultHandler) {

    }

    @Override
    void close() {

    }
}

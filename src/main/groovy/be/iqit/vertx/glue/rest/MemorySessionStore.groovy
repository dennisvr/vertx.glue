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

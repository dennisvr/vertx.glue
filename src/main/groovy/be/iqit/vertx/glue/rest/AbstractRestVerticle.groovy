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

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.FactoryConverter
import groovy.transform.TypeChecked
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.ext.web.sstore.SessionStore

/**
 * Created by dvanroeyen on 30/11/15.
 */
@TypeChecked
abstract class AbstractRestVerticle extends AbstractVerticle {

    int port
    FactoryConverter converter
    Router router

    public AbstractRestVerticle(int port, Converter converter) {
        this.port = port
        this.converter = new FactoryConverter()
                .withDefaultConverter(converter)
                .withConverter(RoutingContext, Session, new SessionConverter())
    }

    @Override
    void start() throws Exception {
        super.start()
        this.router = Router.router(vertx)
        this.router.route().handler(BodyHandler.create())

        router.route().handler(CookieHandler.create());
        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);

        this.configureRoutes();

        vertx.createHttpServer().requestHandler({router.accept(it)}).listen(this.port);
    }

    abstract public void configureRoutes();

    public RouteBuilder get(String path) {
        return new RouteBuilder(this.router, this.converter).get(path)
    }

    public RouteBuilder post(String path) {
        return new RouteBuilder(this.router, this.converter).post(path)
    }

    public RouteBuilder put(String path) {
        return new RouteBuilder(this.router, this.converter).put(path)
    }

    public RouteBuilder delete(String path) {
        return new RouteBuilder(this.router, this.converter).delete(path)
    }

}

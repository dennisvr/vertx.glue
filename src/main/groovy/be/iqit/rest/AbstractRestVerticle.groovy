package be.iqit.rest

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.TypeChecked
import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

/**
 * Created by dvanroeyen on 30/11/15.
 */
@TypeChecked
abstract class AbstractRestVerticle extends AbstractVerticle {

    ObjectMapper objectMapper
    Router router

    public AbstractRestVerticle(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    @Override
    void start() throws Exception {
        super.start()
        this.router = Router.router(vertx)
        this.router.route().handler(BodyHandler.create())

        this.configureRoutes();

        vertx.createHttpServer().requestHandler({router.accept(it)}).listen(7070);
    }

    abstract public void configureRoutes();

    public RouteBuilder get(String path) {
        return new RouteBuilder(this.router, this.objectMapper).get(path)
    }

    public RouteBuilder post(String path) {
        return new RouteBuilder(this.router, this.objectMapper).post(path)
    }

    public RouteBuilder put(String path) {
        return new RouteBuilder(this.router, this.objectMapper).put(path)
    }

    public RouteBuilder delete(String path) {
        return new RouteBuilder(this.router, this.objectMapper).delete(path)
    }

}

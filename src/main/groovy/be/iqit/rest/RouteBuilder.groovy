package be.iqit.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import rx.Observable
import rx.Observer
import rx.functions.Func1

/**
 * Created by dvanroeyen on 01/12/15.
 */
class RouteBuilder {

    ObjectMapper objectMapper
    Router router

    Route route

    Class requestClass
    Class responseClass
    Class paramsClass
    Class errorClass

    public RouteBuilder(Router router, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
        this.router  = router;
    }

    public RouteBuilder get(String path) {
        this.route = this.router.get(path)
        return this
    }

    public RouteBuilder post(String path) {
        this.route = this.router.post(path)
        return this
    }

    public RouteBuilder put(String path) {
        this.route = this.router.put(path)
        return this
    }

    public RouteBuilder delete(String path) {
        this.route = this.router.delete(path)
        return this
    }

    public <RoutingContext> RouteBuilder handle(Handler<RoutingContext> handler) {
        this.route.handler(handler)
        return this
    }


    public <E> RouteBuilder withParams(Class<E> paramsClass) {
        this.paramsClass = paramsClass
        return this
    }

    public <E> RouteBuilder withRequest(Class<E> requestBody) {
        this.requestClass = requestBody
        return this
    }

    public <E> RouteBuilder withResponse(Class<E> responseBody) {
        this.responseClass = responseBody
        return this
    }

    public <E> RouteBuilder withError(Class<E> errorClass) {
        this.errorClass = errorClass
        return this
    }

    public <E> E handle(Closure<E> closure) {
        this.route.handler(new Handler<RoutingContext>() {
            @Override
            void handle(RoutingContext routingContext) {
                def params = toMap(routingContext.request().params())
                if(paramsClass) {
                    params = fromJson(objectMapper.valueToTree(params).toString(), paramsClass)
                }
                Observable.just(routingContext.getBodyAsString())
                        .map(fromJson(requestClass))
                        .flatMap({object -> closure.call(params, object)})
                        .map(toJson(responseClass))
                        .subscribe(observe(routingContext))
            }
        })
    }

    private Observer observe(RoutingContext routingContext) {
        return new Observer() {
            @Override
            void onCompleted() {

            }

            @Override
            void onError(Throwable e) {
                e.printStackTrace()
                HttpServerResponse response = routingContext.response()
                response.setChunked(true)
                response.putHeader("content-type", "application/json")
                response.write(objectMapper.valueToTree(toJson(e, errorClass)).toString())
                response.setStatusCode(404)
                response.end()
            }

            @Override
            void onNext(Object o) {
                HttpServerResponse response = routingContext.response()
                response.setChunked(true)
                response.putHeader("content-type", "application/json")
                response.write(o.toString())
                response.end()
            }
        }
    }


    private <E> Func1<String, E> fromJson(Class<E> clazz) {
        { json ->
            fromJson(json, clazz)
        }
    }

    private <E> E fromJson(String json, Class<E> clazz) {
        return clazz?objectMapper.readValue(json, clazz):json
    }


    private <E> Func1<E,JsonNode> toJson(Class<E> clazz) {
        { E e ->
            toJson(e, clazz)
        }
    }

    private JsonNode toJson(Object e, Class clazz) {
        if(clazz) {
            if(e instanceof Iterable) {
                return new ArrayNode().addAll(e.collect{ objectMapper.valueToTree(objectMapper.convertValue(it, clazz)) })
            } else {
                return objectMapper.valueToTree(objectMapper.convertValue(e, clazz))
            }
        } else  {
            return objectMapper.valueToTree(e)
        }
    }

    private Map<String, Object> toMap(MultiMap multiMap) {
        def Map<String, Object> map = [:]
        multiMap.each { map.put(it.key, it.value)}
        return map
    }


}

package be.iqit.vertx.glue.rest

import be.iqit.vertx.glue.convert.Converter
import com.fasterxml.jackson.databind.JsonNode
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

    Converter converter
    Router router

    Route route

    Class requestClass = String
    Class responseClass = String
    Class paramsClass = HashMap
    Class errorClass = Throwable

    public RouteBuilder(Router router, Converter converter) {
        this.converter = converter
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

    public RouteBuilder handle(Handler<RoutingContext> handler) {
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

    public RouteBuilder withConverter(Converter converter) {
        this.converter = converter
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
                Observable.just(routingContext)
                        .flatMap(toParams())
                        .flatMap({params ->
                            Observable.just(routingContext.getBodyAsString())
                                    .flatMap(fromJson(requestClass))
                                    .flatMap({object -> closure.call(params, object)})
                                    .flatMap(toJson(responseClass))

                        })
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
                String error = e.getMessage()
                HttpServerResponse response = routingContext.response()
                try {
                    response.setChunked(true)
                    response.putHeader("content-type", "application/json")
                    response.setStatusCode(404)
                    error = converter.convert(converter.convert(e, errorClass), String).toBlocking().first()
                } catch(all) {
                    all.printStackTrace()
                    response.setStatusCode(500)
                    error = all.getMessage()
                } finally {
                    response.putHeader("content-type", "plain/text")
                    response.write(error)
                    response.end()
                }

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


    private <E> Func1<String, Observable<E>> fromJson(Class<E> clazz) {
        { json ->
            fromJson(json, clazz)
        }
    }

    private <E> Observable<E> fromJson(String json, Class<E> clazz) {
        return converter.convert(json, clazz)
    }


    private <E> Func1<E,Observable<JsonNode>> toJson(Class<E> clazz) {
        { E e ->
            toJson(e, clazz)
        }
    }

    private Observable<JsonNode> toJson(Object e, Class clazz) {
        return converter.convert(converter.convert(e, clazz), JsonNode)
    }

    private Func1<RoutingContext, Observable> toParams() {
       return { routingContext ->
           Observable.just(toMap(routingContext.request().params()))
                    .flatMap({ params -> converter.convert(params, paramsClass) })
       }

    }

    private Map<String, Object> toMap(MultiMap multiMap) {
        def Map<String, Object> map = [:]
        multiMap.each { map.put(it.key, it.value)}
        return map
    }


}

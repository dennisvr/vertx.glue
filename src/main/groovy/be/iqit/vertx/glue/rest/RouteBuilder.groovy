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
import com.fasterxml.jackson.databind.JsonNode
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import rx.Observable
import rx.Observer
import rx.functions.Func1
import rx.functions.FuncN

/**
 * Created by dvanroeyen on 01/12/15.
 */
class RouteBuilder {

    Converter converter
    Router router

    Route route

    Class responseClass = String
    Class errorClass = Throwable
    Map<Class<? extends  Throwable>, Integer> statusMap = new HashMap<>()

    List<Closure<Observable<Boolean>>> authorizationChain = []

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

    public <T extends Throwable,E> RouteBuilder withError(Class<T> throwableClass, int status) {
        this.statusMap.put(throwableClass, status)
        return this
    }

    /**
     * the closure handling the request.
     *
     * Closure arguments are automatically converted from RoutingContext using the provided Converter
     * String arguments are resolved to the nth path parameter
     *
     * example:
     * given: "/users/:id/:name"
     * { Session session, String userId, Map parameters, String userName, User user -> }
     * Will:
     * - convert RoutingContext to Session
     * - map the "id" parameter to userId (first path parameter)
     * - convert RoutingContext to a Map
     * - map the "name" parameter to userName (second path parameter)
     * - map the RoutingContext to a User
     *
     * @param closure
     * @return
     */
    public <E> E handle(Closure<E> closure) {
       this.route.blockingHandler(new Handler<RoutingContext>() {
            @Override
            void handle(RoutingContext routingContext) {

                Observable.from(authorizationChain).flatMap({ authorizationClosure ->
                    invoke(routingContext, authorizationClosure)
                }).toList().flatMap({
                    invoke(routingContext, closure)
                }).flatMap(toJson(responseClass))
                        .subscribe(observe(routingContext))
            }
        })
    }

    protected <E> Observable<E> invoke(RoutingContext routingContext, Closure<Observable<E>> closure) {
        int paramIndex = 0

        List<String> paramNames  = getParameterNames(routingContext)

        List<Observable<Object>> argumentObservables = closure.parameterTypes.collect {
            if(it.isAssignableFrom(String)) {
                String param = paramNames[paramIndex]
                String value = routingContext.request().params().get(param)
                paramIndex++
                return Observable.just(value)
            }
            converter.convert(routingContext, it)
        }
        Observable.zip(argumentObservables , { arguments ->
            return arguments
        } as FuncN).flatMap({ arguments ->
            closure.call( *arguments )
        })
    }

    protected List<String> getParameterNames(RoutingContext routingContext) {
        def matches = (routingContext.currentRoute().path =~ /:\w+/)
        return matches.collect { String value -> value.substring(1) }

    }

    private Observer observe(RoutingContext routingContext) {
        return new Observer() {
            @Override
            void onCompleted() {

            }

            @Override
            void onError(Throwable e) {
                Integer status = statusMap.get(e.class)
                if(!status) {
                    status = 500
                }
                e.printStackTrace()
                String error = e.getMessage()
                HttpServerResponse response = routingContext.response()
                try {
                    response.setChunked(true)
                    response.putHeader("content-type", "application/json")
                    response.setStatusCode(status)
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

    private <E> Func1<E,Observable<JsonNode>> toJson(Class<E> clazz) {
        { E e ->
            toJson(e, clazz)
        }
    }

    private Observable<JsonNode> toJson(Object e, Class clazz) {
        return converter.convert(converter.convert(e, clazz), JsonNode)
    }

    public RouteBuilder authorize(Closure<Observable<Boolean>> closure) {
        authorizationChain << closure
        return this
    }
}

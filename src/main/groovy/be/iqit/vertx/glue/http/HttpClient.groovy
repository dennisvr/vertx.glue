package be.iqit.bunchbill.http

import be.iqit.vertx.glue.convert.Converter
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import io.vertx.rx.java.ObservableHandler
import io.vertx.rxjava.core.http.HttpClientRequest
import io.vertx.rxjava.core.http.HttpClientResponse

/**
 * Created by dvanroeyen on 12/03/16.
 */
class HttpClient {

    io.vertx.rxjava.core.http.HttpClient delegate
    Converter converter
    String sessionCookie

    public HttpClient(Converter converter, HttpClientOptions options) {
        this.converter = converter
        this.delegate = new io.vertx.rxjava.core.http.HttpClient(Vertx.vertx().createHttpClient(options))
    }

    public HttpClient(Converter converter, int port, String host) {
        this(converter, new HttpClientOptions().setDefaultPort(port).setDefaultHost(host))
    }

    public rx.Observable<HttpClientResponse> get(String url) {
        ObservableHandler observableHandler = new ObservableHandler<HttpClientResponse>(false)
        HttpClientRequest request = delegate.get(url, observableHandler.toHandler())
        request.chunked = true
        if(sessionCookie) {
            String[] cookie = sessionCookie.split(';')
            request.headers().add("Cookie", cookie[0])
        }
        request.end()
        return observableHandler
                .doOnNext({ httpClientResponse ->
            if(!this.sessionCookie) {
                this.sessionCookie = httpClientResponse.delegate.headers()['Set-Cookie']
            }
        })
    }

    public rx.Observable<HttpClientResponse> post(url, payload) {
        ObservableHandler observableHandler = new ObservableHandler<HttpClientResponse>(false)
        HttpClientRequest request = delegate.post(url, observableHandler.toHandler())
        return converter.convert(payload, String.class).flatMap( { body ->
            request.chunked = true
            if(sessionCookie) {
                String[] cookie = sessionCookie.split(';')
                request.headers().add("Cookie", cookie[0])
            }
            request.write(body)
            request.end()
            return observableHandler
        }).doOnNext({ httpClientResponse ->
            if(!this.sessionCookie) {
                this.sessionCookie = httpClientResponse.delegate.headers()['Set-Cookie']
            }
        })
    }

}

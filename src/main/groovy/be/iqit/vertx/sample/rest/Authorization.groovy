package be.iqit.vertx.sample.rest

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

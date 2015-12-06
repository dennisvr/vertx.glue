package be.iqit.vertx.glue.event

import rx.Observable

/**
 * Created by dvanroeyen on 04/12/15.
 */
interface TestService {

    Observable<Integer> foo(String param, Integer number);

    Observable<Integer> throwError();

    Observable<Integer> returnError();

}

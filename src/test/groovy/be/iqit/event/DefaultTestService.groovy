package be.iqit.event

import rx.Observable

/**
 * Created by dvanroeyen on 04/12/15.
 */
class DefaultTestService implements TestService {

    @Override
    Observable<Integer> foo(String param, Integer number) {
        return Observable.just(Integer.parseInt(param)+number)
    }

    @Override
    Observable<Integer> throwError() {
        throw new IllegalArgumentException("test")
    }

    @Override
    Observable<Integer> returnError() {
        return Observable.error("test")
    }
}

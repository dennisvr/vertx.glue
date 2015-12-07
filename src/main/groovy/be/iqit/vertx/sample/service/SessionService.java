package be.iqit.vertx.sample.service;

import be.iqit.vertx.sample.domain.User;
import rx.Observable;

/**
 * Created by dvanroeyen on 06/12/15.
 */
public interface SessionService {

    Observable<User> login(String user, String password);

    Observable<User> logout();
}

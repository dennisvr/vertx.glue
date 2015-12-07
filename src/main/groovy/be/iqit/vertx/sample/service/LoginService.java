package be.iqit.vertx.sample.service;

import be.iqit.vertx.sample.domain.User;
import io.vertx.ext.web.Session;
import rx.Observable;

/**
 * Created by dvanroeyen on 06/12/15.
 */
public interface LoginService {

    Observable<User> login(Session session, String user, String password);

    Observable<Boolean> logout(Session session);
}

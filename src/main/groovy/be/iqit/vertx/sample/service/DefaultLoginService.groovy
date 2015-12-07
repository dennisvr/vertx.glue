package be.iqit.vertx.sample.service

import be.iqit.vertx.sample.domain.User
import be.iqit.vertx.sample.user.service.UserService
import io.vertx.ext.web.Session
import rx.Observable

/**
 * Created by dvanroeyen on 07/12/15.
 */
class DefaultLoginService implements LoginService {

    UserService userService

    public DefaultLoginService(UserService userService) {
        this.userService = userService
    }

    @Override
    Observable<User> login(Session session, String email, String password) {
        return userService.getUserWithEmailAndPassword(email, password).doOnNext({ user ->
            session.data().user = user
        })
    }

    @Override
    Observable<Boolean> logout(Session session) {
        return Observable.just(session).map({ s ->
            s.destroy()
        }).map({
            return true
        })
    }
}

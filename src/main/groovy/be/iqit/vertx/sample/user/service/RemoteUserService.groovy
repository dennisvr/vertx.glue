package be.iqit.vertx.sample.user.service

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.event.EventBuilder
import be.iqit.vertx.sample.domain.User
import io.vertx.core.Vertx
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class RemoteUserService implements UserService {

    Vertx vertx
    Converter converter
    EventBuilder eventBuilder

    public RemoteUserService(Vertx vertx, Converter converter) {
        this.vertx = vertx
        this.converter = converter
        this.eventBuilder = new EventBuilder(UserService,converter, vertx)
    }

    @Override
    Observable<User> getUser(String id) {
        return eventBuilder.send(this.&getUser, User, id)
    }

    @Override
    Observable<User> saveUser(User user) {
        return eventBuilder.send(this.&saveUser, User, user)
    }

    @Override
    Observable<List<User>> getUsers() {
        return eventBuilder.send(this.&getUsers, User)
    }

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return eventBuilder.send(this.&getUserWithEmailAndPassword, User, email, password)
    }
}

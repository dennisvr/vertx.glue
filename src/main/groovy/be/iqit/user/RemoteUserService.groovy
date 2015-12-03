package be.iqit.user

import be.iqit.convert.Converter
import be.iqit.convert.ObjectMapperConverter
import be.iqit.event.EventBuilder
import io.vertx.core.Vertx
import rx.Observable

/**
 * Created by dvanroeyen on 02/12/15.
 */
class RemoteUserService implements UserService {

    Vertx vertx
    Converter converter = new ObjectMapperConverter()
    EventBuilder eventBuilder

    public RemoteUserService(Vertx vertx) {
        this.vertx = vertx
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
}

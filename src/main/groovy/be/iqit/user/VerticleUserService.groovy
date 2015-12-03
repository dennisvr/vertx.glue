package be.iqit.user

import be.iqit.convert.Converter
import be.iqit.convert.ObjectMapperConverter
import be.iqit.event.EventBuilder
import io.vertx.core.AbstractVerticle
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class VerticleUserService extends AbstractVerticle implements UserService {

    UserRepository userRepository
    Converter converter = new ObjectMapperConverter()
    EventBuilder eventBuilder

    public VerticleUserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    void start() throws Exception {
        super.start()
        eventBuilder = new EventBuilder(UserService, converter, vertx)
        eventBuilder.consume(this.&getUser)
        eventBuilder.consume(this.&saveUser)
        eventBuilder.consume(this.&getUsers)
    }

    @Override
    Observable<User> getUser(String id) {
        return userRepository.getUser(id).defaultIfEmpty(null)
                .flatMap({ x ->
                    if(!x) {
                        return Observable.error(new IllegalArgumentException("Unknown user:${id}"))
                    }
                    return Observable.just(x)
                })
    }

    @Override
    Observable<User> saveUser(User user) {
        return Observable.just(user)
    }

    @Override
    Observable<List<User>> getUsers() {
        return userRepository.getUsers().toList()
    }
}

package be.iqit.vertx.sample.user.service

import be.iqit.vertx.sample.user.repository.UserRepository
import be.iqit.vertx.sample.domain.User
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
class DefaultUserService implements UserService {

    UserRepository userRepository

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository
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

    @Override
    Observable<User> getUserWithEmailAndPassword(String email, String password) {
        return this.userRepository.getUserWithEmailAndPassword(email, password)
    }
}

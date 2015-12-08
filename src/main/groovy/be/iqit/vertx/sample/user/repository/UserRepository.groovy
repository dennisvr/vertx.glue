package be.iqit.vertx.sample.user.repository

import be.iqit.vertx.sample.domain.User
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
interface UserRepository {

    Observable<User> getUser(String id)

    Observable<User> getUsers()

    Observable<User> getUserWithEmailAndPassword(String email, String password)

    Observable<User> saveUser(User user)
}

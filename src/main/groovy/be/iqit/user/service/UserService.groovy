package be.iqit.user.service

import be.iqit.user.domain.User
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
interface UserService {

    Observable<User> getUser(String id)

    Observable<User> saveUser(User user)

    Observable<List<User>> getUsers()

}
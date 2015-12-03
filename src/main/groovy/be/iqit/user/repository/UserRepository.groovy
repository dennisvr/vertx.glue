package be.iqit.user.repository

import be.iqit.user.domain.User
import rx.Observable

/**
 * Created by dvanroeyen on 30/11/15.
 */
interface UserRepository {

    Observable<User> getUser(String id)

    Observable<User> getUsers()
}

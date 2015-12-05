package be.iqit.user

import be.iqit.convert.Converter
import be.iqit.convert.ObjectMapperConverter
import be.iqit.event.EventVerticle
import be.iqit.user.domain.User
import be.iqit.user.repository.UserRepository
import be.iqit.user.rest.UserRestVerticle
import be.iqit.user.service.DefaultUserService
import be.iqit.user.service.RemoteUserService
import be.iqit.user.service.UserService
import io.vertx.core.Vertx
import rx.Observable
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by dvanroeyen on 04/12/15.
 */
class UserTest extends Specification {


    @Shared
    Vertx vertx

    @Shared
    Converter converter

    @Shared
    UserRepository userRepository

    @Shared
    RemoteUserService remoteUserService

    @Shared
    DefaultUserService defaultUserService

    @Shared
    EventVerticle eventVerticle

    @Shared
    UserRestVerticle userRestVerticle

    def setup() {
        vertx = Vertx.vertx()
        converter = new ObjectMapperConverter()
        userRepository = Mock(UserRepository)
        remoteUserService = new RemoteUserService(vertx)
        defaultUserService = new DefaultUserService(userRepository)
        eventVerticle = new EventVerticle(UserService, defaultUserService, converter)
        userRestVerticle = new UserRestVerticle(remoteUserService)
        vertx.deployVerticle(eventVerticle)
        vertx.deployVerticle(userRestVerticle)
    }

    def "can get user"() {
        given:
        User user = new User(id:"1")

        when:
        User result = remoteUserService.getUser("1").toBlocking().first()

        then:
        1 * userRepository.getUser(_) >> Observable.just(user)

        then:
        result != null
        result.id == user.id
        result != user //Check if we didn't receive the same instance. This will stop working if we implement an equals method on User

    }

    def "can save user"() {
        given:
        User user = new User(id:"1")

        when:
        User result = remoteUserService.saveUser(user).toBlocking().first()

        then:
        result != null
        result.id == user.id
        result != user //Check if we didn't receive the same instance. This will stop working if we implement an equals method on User

    }


    def "can get users"() {
        given:
        User user1 = new User(id:"1")
        User user2 = new User(id:"2")
        User user3 = new User(id:"3")

        when:
        List<User> result = remoteUserService.getUsers().toBlocking().first()

        then:
        1 * userRepository.getUsers() >> Observable.from([user1, user2, user3])

        then:
        result != null
        result.size() == 3
        result[0].id == "1"
        result[1].id == "2"
        result[2].id == "3"
    }

}

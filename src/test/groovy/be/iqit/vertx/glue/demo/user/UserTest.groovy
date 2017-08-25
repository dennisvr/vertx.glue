package be.iqit.vertx.glue.demo.user

import be.iqit.vertx.glue.event.EventVerticle
import be.iqit.vertx.glue.GlueBuilder
import be.iqit.vertx.glue.event.VertxEventConsumer
import be.iqit.vertx.glue.event.VertxEventSender
import be.iqit.vertx.glue.common.domain.User
import be.iqit.vertx.glue.demo.rest.UserRestVerticle
import be.iqit.vertx.glue.common.repository.MongoUserRepository
import be.iqit.vertx.glue.common.repository.UserRepository
import be.iqit.vertx.glue.common.service.DefaultUserService
import be.iqit.vertx.glue.common.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.ObjectMapperConverter
import be.iqit.vertx.glue.mongo.Repository
import be.iqit.vertx.glue.paging.Page
import io.vertx.core.Vertx
import org.bson.Document
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
    UserService remoteUserService

    @Shared
    DefaultUserService defaultUserService

    @Shared
    EventVerticle eventVerticle

    @Shared
    UserRestVerticle userRestVerticle

    @Shared
    Repository mongoRepository

    def setup() {
        vertx = Vertx.vertx()
        converter = new ObjectMapperConverter()
        mongoRepository = Mock(Repository)
        userRepository = new MongoUserRepository(mongoRepository)
        VertxEventSender eventSender = new VertxEventSender(vertx, converter)
        VertxEventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)
        GlueBuilder glueBuilder = new GlueBuilder(eventSender, eventConsumer)
        remoteUserService = glueBuilder.createRemote(UserService)
        defaultUserService = new DefaultUserService(userRepository)
        eventVerticle = glueBuilder.createVerticle(UserService, defaultUserService)
        userRestVerticle = new UserRestVerticle(remoteUserService, converter)
        vertx.deployVerticle(eventVerticle)
        vertx.deployVerticle(userRestVerticle)
    }

    def "can get user"() {
        given:
        Document user = new Document([id:"1"])

        when:
        User result = remoteUserService.getUser("1").toBlocking().first()

        then:
        1 * mongoRepository.find(_) >> Observable.just(user)

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
        1 * mongoRepository.save(_, _) >> Observable.just(user)

        then:
        result != null
        result.id == user.id
        result != user //Check if we didn't receive the same instance. This will stop working if we implement an equals method on User

    }

    def "can get users"() {
        given:
        Map user1 = [id:"1"]
        Map user2 = [id:"2"]
        Map user3 = [id:"3"]

        when:
        List<User> result = remoteUserService.getUsers().toBlocking().first()

        then:
        1 * mongoRepository.find() >> Observable.from([user1, user2, user3])

        then:
        result != null
        result.size() == 3
        result[0].id == "1"
        result[1].id == "2"
        result[2].id == "3"
    }

    def "can get users page"() {
        given:
        Map user1 = [id:"1"]
        Map user2 = [id:"2"]
        Map user3 = [id:"3"]

        when:
        Page<User> result = remoteUserService.getUserPage().toBlocking().first()

        then:
        1 * mongoRepository.find() >> Observable.from([user1, user2, user3])

        then:
        result != null
        result.total == 3
        result.items[0].id == "1"
        result.items[1].id == "2"
        result.items[2].id == "3"
    }

}

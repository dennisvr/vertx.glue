package be.iqit.vertx.sample

import be.iqit.vertx.glue.event.EventConsumer
import be.iqit.vertx.glue.event.EventSender
import be.iqit.vertx.glue.event.VertxEventConsumer
import be.iqit.vertx.glue.event.VertxEventSender
import be.iqit.vertx.glue.event.EventVerticle
import be.iqit.vertx.glue.GlueBuilder
import be.iqit.vertx.sample.rest.LoginRestVerticle
import be.iqit.vertx.sample.service.DefaultLoginService
import be.iqit.vertx.sample.user.repository.UserRepository
import be.iqit.vertx.sample.user.service.DefaultUserService
import be.iqit.vertx.sample.user.repository.MongoUserRepository
import be.iqit.vertx.sample.user.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.ObjectMapperConverter
import be.iqit.vertx.glue.mongo.DefaultMongoRepository
import be.iqit.vertx.glue.mongo.MongoRepository
import be.iqit.vertx.sample.rest.UserRestVerticle
import com.mongodb.async.client.MongoClient
import com.mongodb.async.client.MongoClients
import com.mongodb.async.client.MongoDatabase
import io.vertx.core.Vertx

/**
 * Created by dvanroeyen on 30/11/15.
 */
class Starter {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")
        MongoDatabase mongoDatabase = mongoClient.getDatabase("didditVle")
        Vertx vertx = Vertx.vertx()

        Converter converter = new ObjectMapperConverter()

        EventSender eventSender = new VertxEventSender(vertx, converter)
        EventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)

        GlueBuilder glueBuilder = new GlueBuilder(eventSender, eventConsumer)

        MongoRepository mongoRepository = new DefaultMongoRepository(mongoDatabase.getCollection("users"))
        UserRepository userRepository = new MongoUserRepository(mongoRepository, converter)
        DefaultUserService defaultUserService = new DefaultUserService(userRepository)

        UserService remoteUserService = glueBuilder.createRemote(UserService)
        EventVerticle userEventVerticle = glueBuilder.createVerticle(UserService, defaultUserService)

        UserRestVerticle userRestVerticle = new UserRestVerticle(remoteUserService, converter)

        DefaultLoginService defaultLoginService = new DefaultLoginService(remoteUserService)
        LoginRestVerticle loginRestVerticle = new LoginRestVerticle(defaultLoginService, converter)

        vertx.deployVerticle(userEventVerticle)
        vertx.deployVerticle(userRestVerticle)
        vertx.deployVerticle(loginRestVerticle)
    }
}

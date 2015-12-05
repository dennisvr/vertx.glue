package be.iqit.vertx3

import be.iqit.convert.ObjectMapperConverter
import be.iqit.event.EventVerticle
import be.iqit.user.service.DefaultUserService
import be.iqit.user.repository.MongoUserRepository
import be.iqit.user.service.RemoteUserService
import be.iqit.user.rest.UserRestVerticle
import be.iqit.user.service.UserService
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

        DefaultUserService defaultUserService = new DefaultUserService(new MongoUserRepository(mongoDatabase))
        RemoteUserService remoteUserService = new RemoteUserService(vertx)
        UserRestVerticle userRestVerticle = new UserRestVerticle(remoteUserService)
        EventVerticle userEventVerticle = new EventVerticle(UserService, defaultUserService, new ObjectMapperConverter())

        vertx.deployVerticle(userEventVerticle)
        vertx.deployVerticle(userRestVerticle)
    }
}

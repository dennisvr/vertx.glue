package be.iqit.vertx3

import be.iqit.user.MongoUserRepository
import be.iqit.user.RemoteUserService
import be.iqit.user.UserRestVerticle
import be.iqit.user.VerticleUserService
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
        VerticleUserService userService = new VerticleUserService(new MongoUserRepository(mongoDatabase))
        RemoteUserService remoteUserService = new RemoteUserService(vertx)
        UserRestVerticle restVerticle = new UserRestVerticle(remoteUserService)

        vertx.deployVerticle(userService)
        vertx.deployVerticle(restVerticle)
    }
}

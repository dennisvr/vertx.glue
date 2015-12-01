package be.iqit.vertx3

import be.iqit.user.MongoUserRepository
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
        VerticleUserService userService = new VerticleUserService(new MongoUserRepository(mongoDatabase))
        UserRestVerticle restVerticle = new UserRestVerticle(userService)
        Vertx vertx = Vertx.vertx()
        vertx.deployVerticle(userService)
        vertx.deployVerticle(restVerticle)
    }
}

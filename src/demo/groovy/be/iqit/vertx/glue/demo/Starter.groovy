/**
 * Copyright 2015 Dennis Van Roeyen, iQit, BVBA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package be.iqit.vertx.glue.demo

import be.iqit.vertx.glue.common.domain.User
import be.iqit.vertx.glue.event.EventConsumer
import be.iqit.vertx.glue.event.EventSender
import be.iqit.vertx.glue.event.VertxEventConsumer
import be.iqit.vertx.glue.event.VertxEventSender
import be.iqit.vertx.glue.event.EventVerticle
import be.iqit.vertx.glue.GlueBuilder
import be.iqit.vertx.glue.demo.rest.LoginRestVerticle
import be.iqit.vertx.glue.common.service.DefaultLoginService
import be.iqit.vertx.glue.common.repository.UserRepository
import be.iqit.vertx.glue.common.service.DefaultUserService
import be.iqit.vertx.glue.common.repository.MongoUserRepository
import be.iqit.vertx.glue.common.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.convert.ObjectMapperConverter
import be.iqit.vertx.glue.mongo.MongoRepository
import be.iqit.vertx.glue.mongo.Repository
import be.iqit.vertx.glue.demo.rest.UserRestVerticle
import com.mongodb.rx.client.MongoClient
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.MongoDatabase
import io.vertx.core.Vertx

/**
 * Created by dvanroeyen on 30/11/15.
 */
class Starter {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glue")
        Vertx vertx = Vertx.vertx()

        Converter converter = new ObjectMapperConverter()

        EventSender eventSender = new VertxEventSender(vertx, converter)
        EventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)

        GlueBuilder glueBuilder = new GlueBuilder(eventSender, eventConsumer)

        Repository mongoRepository = new MongoRepository(mongoDatabase.getCollection("users", User))
        UserRepository userRepository = new MongoUserRepository(mongoRepository)
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

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
package be.iqit.vertx.glue.demo.rest

import be.iqit.vertx.glue.rest.AbstractRestVerticle
import be.iqit.vertx.glue.rest.ErrorDTO
import be.iqit.vertx.glue.rest.RoutingContextBodyConverter
import be.iqit.vertx.glue.rest.RoutingContextParamsConverter
import be.iqit.vertx.glue.common.domain.User
import be.iqit.vertx.glue.common.dto.UserDTO
import be.iqit.vertx.glue.common.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.common.domain.UserFilter
import groovy.transform.TypeChecked
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session

/**
 * Created by dvanroeyen on 01/12/15.
 */
@TypeChecked
class UserRestVerticle extends AbstractRestVerticle {

    UserService userService

    UserRestVerticle(UserService userService, Converter converter) {
        super(7072, converter)
        this.userService = userService
        this.converter.withConverter(RoutingContext, UserFilter, new RoutingContextParamsConverter(this.converter))
        this.converter.withConverter(RoutingContext, User, new RoutingContextBodyConverter(this.converter))
    }

    @Override
    void configureRoutes() {

        get("/users/:id")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .authorize(Authorization.IS_USER)
                .handle { Session session, String id ->
                    return userService.getUser(id)
                }

        get("/users")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .authorize(Authorization.IS_USER)
                .handle { Session session, UserFilter filter ->
                    return userService.getUsers()
                }

        post("/users/:id")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .authorize(Authorization.IS_ADMIN)
                .handle({ Session session, User user ->
                    return userService.saveUser(user)
                })
    }

}
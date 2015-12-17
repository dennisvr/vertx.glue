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
package be.iqit.vertx.sample.rest

import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.glue.rest.AbstractRestVerticle
import be.iqit.vertx.glue.rest.ErrorDTO
import be.iqit.vertx.glue.rest.RoutingContextBodyConverter
import be.iqit.vertx.sample.rest.command.LoginCommand
import be.iqit.vertx.sample.service.LoginService
import be.iqit.vertx.sample.user.dto.UserDTO
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session

/**
 * Created by dvanroeyen on 07/12/15.
 */
class LoginRestVerticle extends AbstractRestVerticle {

    LoginService loginService

    public LoginRestVerticle(LoginService loginService, Converter converter) {
        super(7071, converter)
        this.loginService = loginService
        this.converter.withConverter(RoutingContext, LoginCommand, new RoutingContextBodyConverter(this.converter))
    }

    @Override
    void configureRoutes() {

        post("/login")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .handle { Session session, LoginCommand loginCommand ->
            return loginService.login(session, loginCommand.email, loginCommand.password)
        }

        post("/logout")
                .handle { Session session ->
            return loginService.logout(session)
        }

    }

}
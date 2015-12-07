package be.iqit.vertx.sample.rest

import be.iqit.vertx.glue.rest.AbstractRestVerticle
import be.iqit.vertx.glue.rest.ErrorDTO
import be.iqit.vertx.glue.rest.RoutingContextParamsConverter
import be.iqit.vertx.sample.user.dto.UserDTO
import be.iqit.vertx.sample.user.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.sample.user.domain.UserFilter
import io.vertx.ext.web.RoutingContext

/**
 * Created by dvanroeyen on 01/12/15.
 */
class UserRestVerticle extends AbstractRestVerticle {

    UserService userService

    public UserRestVerticle(UserService userService, Converter converter) {
        super(7070, converter)
        this.userService = userService
        this.converter.withConverter(RoutingContext, UserFilter, new RoutingContextParamsConverter(this.converter))
    }

    @Override
    void configureRoutes() {

        get("/users/:id")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .handle { UserFilter filter ->
                    return userService.getUser(filter.id)
                }

        get("/users")
                .withResponse(UserDTO)
                .handle { UserFilter filter ->
                    return userService.getUsers()
                }

        post("/users/:id")
                .withResponse(UserDTO)
                .handle(userService.&saveUser)
    }
}
package be.iqit.vertx.sample.user

import be.iqit.vertx.glue.rest.AbstractRestVerticle
import be.iqit.vertx.glue.rest.ErrorDTO
import be.iqit.vertx.sample.domain.User
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
        super(converter)
        this.userService = userService
        this.converter.withConverter(RoutingContext, UserFilter, new UserFilterConverter(this.converter))
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
package be.iqit.vertx.sample.user.rest

import be.iqit.vertx.glue.rest.AbstractRestVerticle
import be.iqit.vertx.glue.rest.ErrorDTO
import be.iqit.vertx.sample.user.domain.User
import be.iqit.vertx.sample.user.dto.UserDTO
import be.iqit.vertx.sample.user.service.UserService
import be.iqit.vertx.glue.convert.Converter
import be.iqit.vertx.sample.user.domain.UserFilter

/**
 * Created by dvanroeyen on 01/12/15.
 */
class UserRestVerticle extends AbstractRestVerticle {

    UserService userService

    public UserRestVerticle(UserService userService, Converter converter) {
        super(converter)
        this.userService = userService
    }

    @Override
    void configureRoutes() {

        get("/users/:id")
                .withParams(UserFilter)
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .handle { UserFilter filter, body ->
                    return userService.getUser(filter.id)
                }

        get("/users")
                .withParams(UserFilter)
                .withResponse(UserDTO)
                .handle { UserFilter filter, body ->
                    return userService.getUsers().toList()
                }

        post("/users/:id")
                .withRequest(User)
                .withResponse(UserDTO)
                .handle { params, User user ->
                    return userService.saveUser(user)
                }


    }
}
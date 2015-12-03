package be.iqit.user.rest

import be.iqit.rest.AbstractRestVerticle
import be.iqit.rest.ErrorDTO
import be.iqit.user.domain.User
import be.iqit.user.domain.UserFilter
import be.iqit.user.dto.UserDTO
import be.iqit.user.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by dvanroeyen on 01/12/15.
 */
class UserRestVerticle extends AbstractRestVerticle {

    UserService userService

    public UserRestVerticle(UserService userService) {
        super(new ObjectMapper())
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
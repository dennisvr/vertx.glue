# README #

Experimental vertx + RXJava + Mongo stack

### Build ###

./gradlew clean build

# Concepts #

## Converter ##

### Goal ###
Provide a framework for converting object from and to other representations.

### Example ###

```
#!groovy

    def "can convert from String"() {
        given:
        String value = '{"id":1,"name":"test"}'

        when:
        TestDomainA domain = converter.convert(value, TestDomainA).toBlocking().first()

        then:
        domain != null
        domain.id == 1
        domain.name == 'test'
    }
```


## RouteBuilder ##

### Goal ###
Provide a simple way to serve http requests including Conversion logic

### Example ###


```
#!groovy

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
                    return userService.getUsers()
                }

        post("/users/:id")
                .withRequest(User)
                .withResponse(UserDTO)
                .handle { params, User user ->
                    return userService.saveUser(user)
                }


    }
}
```
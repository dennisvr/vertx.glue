# vertx.glue #

vertx.glue offers a few lightweight concepts to facilitate the development of vertx applications. These concepts take away the boilerplate coding that's involved in developing a vertx application with a REST interface and integration with Mongo.  

# Concepts #

## GlueBuilder ##

Given an interface and it's implementation, GlueBuilder creates a Verticle for the implementation and a Remote implementation to interact with the Verticle.

In this example we have a UserService interface with a DefaultUserService implementation. With GlueBuilder we create a Verticle and a Remote implementation of the UserService to communicate with the Verticle. We pass the remote implementation to a RestVerticle.

```
#!groovy

        Vertx vertx = Vertx.vertx()

        Converter converter = new ObjectMapperConverter()

        EventSender eventSender = new VertxEventSender(vertx, converter)
        EventConsumer eventConsumer = new VertxEventConsumer(vertx, converter)

        GlueBuilder glueBuilder = new GlueBuilder(eventSender, eventConsumer)

        DefaultUserService defaultUserService = new DefaultUserService()

        Verticle userVerticle = glueBuilder.createVerticle(UserService, defaultUserService)
        UserService remoteUserService = glueBuilder.createRemote(UserService)

        UserRestVerticle userRestVerticle = new UserRestVerticle(remoteUserService, converter)

        vertx.deployVerticle(userVerticle)
        vertx.deployVerticle(userRestVerticle)

```


## RouteBuilder ##

### Goal ###
Provide a simple way to serve http requests. RoutingContext is automatically converted to every argument of the Closure passed as handler. Underlying this will use the Converter framework.

### Example ###


```
#!groovy

class UserRestVerticle extends AbstractRestVerticle {

    UserService userService

    public UserRestVerticle(UserService userService, Converter converter) {
        super(converter)
        this.userService = userService
        
        //Add a RoutingContext to UserFilter converter
        this.converter.withConverter(RoutingContext, UserFilter, new UserFilterConverter(this.converter))
    }

    @Override
    void configureRoutes() {

        get("/users/:id")
                .withResponse(UserDTO)
                .withError(ErrorDTO)
                .authorize(Authorization.IS_USER)
                .handle { UserFilter filter ->
                    return userService.getUser(filter.id)
                }

        get("/users")
                .withResponse(UserDTO)
                .authorize(Authorization.IS_USER)
                .handle { UserFilter filter ->
                    return userService.getUsers()
                }

        post("/users/:id")
                .withResponse(UserDTO)
                .authorize(Authorization.IS_ADMIN)
                .handle(userService.&saveUser)

    }
}
```

## Converter ##

### Goal ###
Provide Observable methods for converting objects from and to other representations. Typically used to convert from and to JSON but supports conversion from and to any object if an implementation is present. 

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

Wrapping a Converter implementation in a FactoryConverter allows to register new Converters. FactoryConverter will search for a suitable converter when no specific converter is found. If no suitable converter is found FactoryConverter will delegate to the underlying converter.

```
#!groovy

    public LoginRestVerticle(LoginService loginService, Converter converter) {
        this.converter = new FactoryConverter(converter)
        this.converter.withConverter(RoutingContext, LoginCommand, new RoutingContextBodyConverter(this.converter))
    }
```
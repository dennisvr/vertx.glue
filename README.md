# vertx.glue #

vertx.glue offers a few lightweight concepts to facilitate the development of vertx applications. These concepts take away the boilerplate coding that's involved in developing a vertx application with a REST interface and integration with Mongo.  

# Concepts #

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
```

## EventBuilder ##

### Goal ###
Provide a simple way to send and consume events on the Vertx EventBus. 

### Example ###
In this example we have a UserService with a Remote implementation and a Verticle implementation. Ideally the Verticle doesn't implement the UserService but delegates the calls to a DefaultUserService implementation. A better approach is to use a Verticle implementation which takes an object and exposes it's method using EventBuilder.consume The DefaultVerticle implementation does exactly this.

```
#!groovy

class RemoteUserService implements UserService {

    Vertx vertx
    Converter converter = new ObjectMapperConverter()
    EventBuilder eventBuilder

    public RemoteUserService(Vertx vertx) {
        this.vertx = vertx
        this.eventBuilder = new EventBuilder(UserService,converter, vertx)
    }

    @Override
    Observable<User> getUser(String id) {
        return eventBuilder.send(this.&getUser, User, id)
    }
}

class VerticleUserService extends AbstractVerticle implements UserService {

    UserRepository userRepository
    Converter converter = new ObjectMapperConverter()
    EventBuilder eventBuilder

    public VerticleUserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    void start() throws Exception {
        super.start()
        eventBuilder = new EventBuilder(UserService, converter, vertx)
        eventBuilder.consume(this.&getUser)
    }

    @Override
    Observable<User> getUser(String id) {
        return userRepository.getUser(id)
    }


}




```

## Converter ##

### Goal ###
Provide Observable methods for converting objects from and to other representations. Typically used to convert from and to JSON but supports conversion from and to any object.

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
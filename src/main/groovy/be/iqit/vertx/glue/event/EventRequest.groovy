package be.iqit.vertx.glue.event

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventRequest {

    List<EventParameter> parameters = []

    public EventRequest() {

    }

    public EventRequest(Object... objects) {
        objects.each { object ->
            parameters << new EventParameter(object?.getClass(), object)
        }
    }
}

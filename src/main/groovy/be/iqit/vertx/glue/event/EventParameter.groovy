package be.iqit.vertx.glue.event

/**
 * Created by dvanroeyen on 03/12/15.
 */
class EventParameter {

    Class clazz
    Object value

    public EventParameter() {

    }

    public EventParameter(Class clazz, Object value) {
        this.clazz = clazz
        this.value = value
    }

}

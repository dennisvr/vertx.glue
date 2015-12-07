package be.iqit.vertx.glue.convert


/**
 * Created by dvanroeyen on 07/12/15.
 */
abstract class DelegatingConverter<I,O> extends AbstractConverter<I,O> {

    Converter converter

    public DelegatingConverter(Converter converter) {
        this.converter = converter
    }

}

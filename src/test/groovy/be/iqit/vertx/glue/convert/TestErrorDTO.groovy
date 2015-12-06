package be.iqit.vertx.glue.convert

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by dvanroeyen on 02/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TestErrorDTO {

    String message
}

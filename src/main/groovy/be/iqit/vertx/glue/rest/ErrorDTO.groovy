package be.iqit.vertx.glue.rest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by dvanroeyen on 01/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ErrorDTO {

    String message
}

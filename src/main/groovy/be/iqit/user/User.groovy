package be.iqit.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by dvanroeyen on 30/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class User {

    String id

}

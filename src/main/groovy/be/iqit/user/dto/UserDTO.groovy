package be.iqit.user.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Created by dvanroeyen on 01/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class UserDTO {

    String id
    String discriminator

}

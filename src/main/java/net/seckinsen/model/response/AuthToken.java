package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by seck on 30.08.2017.
 */

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken {

    private String token;

}

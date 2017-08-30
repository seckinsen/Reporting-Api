package net.seckinsen.model.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by seck on 30.08.2017.
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "credential")
public class CredentialProperty {

    private String email;

    private String password;

}

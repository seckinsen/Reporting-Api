package net.seckinsen.model.request;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by seck on 30.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialsDto {

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}

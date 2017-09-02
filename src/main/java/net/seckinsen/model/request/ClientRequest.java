package net.seckinsen.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientRequest {

    @NotBlank(message = "Transaction Id cannot be blank")
    private String transactionId;

}

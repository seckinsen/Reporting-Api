package net.seckinsen.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@Builder
public class ClientRequest {

    @NotBlank(message = "Transaction Id cannot be blank")
    private String transactionId;

}

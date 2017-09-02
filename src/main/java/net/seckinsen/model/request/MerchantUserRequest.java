package net.seckinsen.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantUserRequest {

    @NotNull(message = "Merchant user identifier cannot be null")
    private Integer id;

}

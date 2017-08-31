package net.seckinsen.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@Builder
public class MerchantUserRequest {

    @NotNull(message = "Merchant user identifier cannot be null")
    private Integer id;

}

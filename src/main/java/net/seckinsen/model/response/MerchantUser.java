package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantUser {

    private Integer id;

    private String role;

    private String email;

    private String name;

    private Integer merchantId;

    private String secretKey;

}

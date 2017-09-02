package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientResponse {

    private CustomerInfo customerInfo;

}

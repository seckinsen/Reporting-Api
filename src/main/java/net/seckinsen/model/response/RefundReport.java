package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by seck on 01.09.2017.
 */

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundReport {

    private Integer count;

    private Long total;

    private String currency;

}

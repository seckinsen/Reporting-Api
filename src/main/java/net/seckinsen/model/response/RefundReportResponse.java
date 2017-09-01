package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by seck on 01.09.2017.
 */

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundReportResponse {

    private String status;

    @JsonProperty("response")
    private List<RefundReport> refundReports;

}

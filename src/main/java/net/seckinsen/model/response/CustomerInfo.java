package net.seckinsen.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInfo {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("deleted_at")
    private Date deletedAt;

    private String number;

    private String expiryMonth;

    private String expiryYear;

    private String startMonth;

    private String startYear;

    private String issueNumber;

    private String email;

    private String birthday;

    private String gender;

    private String billingTitle;

    private String billingFirstName;

    private String billingLastName;

    private String billingCompany;

    private String billingAddress1;

    private String billingAddress2;

    private String billingCity;

    private String billingPostcode;

    private String billingState;

    private String billingCountry;

    private String billingPhone;

    private String billingFax;

    private String shippingTitle;

    private String shippingFirstName;

    private String shippingLastName;

    private String shippingCompany;

    private String shippingAddress1;

    private String shippingAddress2;

    private String shippingCity;

    private String shippingPostcode;

    private String shippingState;

    private String shippingCountry;

    private String shippingPhone;

    private String shippingFax;

}

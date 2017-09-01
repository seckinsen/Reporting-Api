package net.seckinsen.controller;

import net.seckinsen.model.error.ApiError;
import net.seckinsen.model.error.AuthorizationError;
import net.seckinsen.model.error.ErrorResponse;
import net.seckinsen.model.request.RefundsReportRequest;
import net.seckinsen.model.response.RefundReportResponse;
import net.seckinsen.service.ReportService;
import net.seckinsen.util.ErrorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by seck on 01.09.2017.
 */

@RestController
public class ReportController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/refunds/report", produces = "application/json; charset=UTF-8")
    public ResponseEntity refundsReport(@RequestHeader(name = "Authorization", required = false) String authToken,
                                        @RequestBody @Valid RefundsReportRequest refundsReportRequest,
                                        BindingResult bindingResult) {

        log.info("Refunds report request attempt -> ( Report fromDate : {} - toDate : {} - merchant : {} - acquirer : {} ) - Authorization ( {} )",
                refundsReportRequest.getFromDate(),
                refundsReportRequest.getToDate(),
                refundsReportRequest.getMerchant(),
                refundsReportRequest.getAcquirer(),
                authToken);

        if (authToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Missed!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(ErrorUtils.getBindingResultErrors(bindingResult)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Optional<RefundReportResponse> refundReportResponseOptional = reportService.getRefundsReport(refundsReportRequest, authToken);

            if (!refundReportResponseOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponse(Stream.of(new ApiError()).collect(Collectors.toList())), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(refundReportResponseOptional.get(), HttpStatus.OK);
        } catch (HttpClientErrorException exp) {
            log.error("Api was called wrongly -> status : {} - message : {}", exp.getStatusText(), exp.getMessage());
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Expired!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }

    }

}

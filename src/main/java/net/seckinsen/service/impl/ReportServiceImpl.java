package net.seckinsen.service.impl;

import net.seckinsen.model.request.RefundsReportRequest;
import net.seckinsen.model.response.RefundReportResponse;
import net.seckinsen.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Created by seck on 01.09.2017.
 */

@Service
public class ReportServiceImpl implements ReportService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.report.refunds}")
    private String refundsReportPath;

    @Autowired
    public ReportServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<RefundReportResponse> getRefundsReport(RefundsReportRequest refundsReportRequest, String authToken) {

        String url = baseUrl + refundsReportPath;
        ResponseEntity<RefundReportResponse> responseEntity;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        try {
            log.info("Get refunds report service was called -> {} - ( fromDate : {} - toDate : {} - merchant : {} - acquirer : {} ) - token ( {} )",
                    url,
                    refundsReportRequest.getFromDate(),
                    refundsReportRequest.getToDate(),
                    refundsReportRequest.getMerchant(),
                    refundsReportRequest.getAcquirer(),
                    authToken);
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class);
        } catch (HttpServerErrorException exp) {
            log.error("Api was called wrongly -> status : {} - body : {}", exp.getStatusText(), exp.getResponseBodyAsString());
            return Optional.empty();
        } catch (HttpMessageNotReadableException exp) {
            log.error("Api was called wrongly -> message : {}", exp.getMessage());
            return Optional.empty();
        }

        return Optional.of(responseEntity.getBody());

    }

}

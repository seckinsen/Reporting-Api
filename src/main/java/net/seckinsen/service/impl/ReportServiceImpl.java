package net.seckinsen.service.impl;

import net.seckinsen.configuration.properties.ReportServiceProperties;
import net.seckinsen.model.request.RefundsReportRequest;
import net.seckinsen.model.response.RefundReportResponse;
import net.seckinsen.service.ReportService;
import net.seckinsen.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by seck on 01.09.2017.
 */

@Service
public class ReportServiceImpl implements ReportService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    private final String url;

    @Autowired
    public ReportServiceImpl(RestTemplate restTemplate, ReportServiceProperties properties) {
        this.restTemplate = restTemplate;
        Objects.requireNonNull(properties.getUrl(), "Url cannot be null");
        url = properties.getUrl();
    }

    @Override
    public Optional<RefundReportResponse> getRefundsReport(RefundsReportRequest refundsReportRequest, String authToken) {

        log.info("Getting refunds report service was called -> {} - ( fromDate : {} - toDate : {} - merchant : {} - acquirer : {} ) - token ( {} )",
                url,
                refundsReportRequest.getFromDate(),
                refundsReportRequest.getToDate(),
                refundsReportRequest.getMerchant(),
                refundsReportRequest.getAcquirer(),
                authToken);

        RefundReportResponse refundReportResponse = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, HttpUtils.generateAuthorizationHeader(authToken)), RefundReportResponse.class).getBody();

        return Optional.of(refundReportResponse);

    }

}

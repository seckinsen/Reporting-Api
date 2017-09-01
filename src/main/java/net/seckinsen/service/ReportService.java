package net.seckinsen.service;

import net.seckinsen.model.request.RefundsReportRequest;
import net.seckinsen.model.response.RefundReportResponse;

import java.util.Optional;

/**
 * Created by seck on 01.09.2017.
 */

public interface ReportService {

    Optional<RefundReportResponse> getRefundsReport(RefundsReportRequest refundsReportRequest, String authToken);

}

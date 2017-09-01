package net.seckinsen.service;

import net.seckinsen.model.request.RefundsReportRequest;
import net.seckinsen.model.response.RefundReport;
import net.seckinsen.model.response.RefundReportResponse;
import net.seckinsen.service.Impl.ReportServiceImpl;
import net.seckinsen.util.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by seck on 01.09.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private RestTemplate restTemplateMock;

    private ReportService reportService;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.report.refunds}")
    private String refundsReportPath;

    private final String url = baseUrl + refundsReportPath;

    @Before
    public void setUp() {
        reportService = new ReportServiceImpl(restTemplateMock);
    }

    @Test
    public void getRefundsReportWithValidRefundsReportRequestAndTokenShouldReturnRefundsReportResponse() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        RefundsReportRequest refundsReportRequest = RefundsReportRequest.builder()
                .fromDate(Calendar.getInstance().getTime())
                .toDate(Calendar.getInstance().getTime())
                .merchant(2)
                .acquirer(5)
                .build();

        RefundReport refundReport = RefundReport.builder()
                .count(283)
                .total((long) 28300)
                .currency("USD")
                .build();

        RefundReport refundReport2 = RefundReport.builder()
                .count(11)
                .total((long) 110)
                .currency("EUR")
                .build();

        RefundReportResponse refundReportResponse = RefundReportResponse.builder()
                .status("APPROVED")
                .refundReports(Arrays.asList(refundReport, refundReport2))
                .build();

        // WHEN
        when(restTemplateMock.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class))
                .thenReturn(new ResponseEntity<>(refundReportResponse, HttpStatus.OK));

        Optional<RefundReportResponse> refundReportResponseOptional = reportService.getRefundsReport(refundsReportRequest, authToken);

        // THEN
        verify(restTemplateMock, times(1)).exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class);
        assertTrue("Fault [expected true]", refundReportResponseOptional.isPresent());
        assertEquals("Fault [expected 'Status' equals]",
                "APPROVED",
                refundReportResponseOptional.get().getStatus());
        assertEquals("Fault [expected 'Refunds Report Size' equals]",
                2,
                refundReportResponseOptional.get().getRefundReports().size());
        assertEquals("Fault [expected 'First Refunds Report Currency Type' equals]",
                "USD",
                refundReportResponseOptional.get().getRefundReports().get(0).getCurrency());
        assertEquals("Fault [expected 'Second Refunds Report Total Amount' equals]",
                Long.valueOf(110),
                refundReportResponseOptional.get().getRefundReports().get(1).getTotal());
    }

    @Test
    public void getRefundsReportWithValidRefundsReportRequestAndInvalidTokenShouldThrowException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String expectedExceptionMessage = "401 UNAUTHORIZED";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        RefundsReportRequest refundsReportRequest = RefundsReportRequest.builder()
                .fromDate(Calendar.getInstance().getTime())
                .toDate(Calendar.getInstance().getTime())
                .merchant(2)
                .acquirer(5)
                .build();

        // WHEN
        when(restTemplateMock.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            reportService.getRefundsReport(refundsReportRequest, authToken);
            fail("HttpClientErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getRefundsReportWithInvalidRefundsReportRequestAndValidTokenShouldReturnEmptyOptionalInstance() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        RefundsReportRequest refundsReportRequest = RefundsReportRequest.builder()
                .fromDate(Calendar.getInstance().getTime())
                .toDate(Calendar.getInstance().getTime())
                .merchant(2)
                .acquirer(5)
                .build();

        // WHEN
        when(restTemplateMock.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));


        Optional<RefundReportResponse> refundReportResponseOptional = reportService.getRefundsReport(refundsReportRequest, authToken);

        // THEN
        verify(restTemplateMock, times(1)).exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class);
        assertFalse("Fault [expected false]", refundReportResponseOptional.isPresent());
    }

    @Test
    public void getRefundsReportWithNotReadableRefundsReportRequestAndValidTokenShouldReturnEmptyOptionalInstance() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        RefundsReportRequest refundsReportRequest = RefundsReportRequest.builder()
                .fromDate(Calendar.getInstance().getTime())
                .toDate(Calendar.getInstance().getTime())
                .merchant(2)
                .acquirer(5)
                .build();

        // WHEN
        when(restTemplateMock.exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class))
                .thenThrow(new HttpMessageNotReadableException("Not Readable Refunds Report Request"));


        Optional<RefundReportResponse> refundReportResponseOptional = reportService.getRefundsReport(refundsReportRequest, authToken);

        // THEN
        verify(restTemplateMock, times(1)).exchange(url, HttpMethod.POST, new HttpEntity<>(refundsReportRequest, headers), RefundReportResponse.class);
        assertFalse("Fault [expected false]", refundReportResponseOptional.isPresent());
    }

}

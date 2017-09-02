package net.seckinsen.service;

import net.seckinsen.configuration.properties.ClientServiceProperties;
import net.seckinsen.exception.NullCustomerInfoException;
import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;
import net.seckinsen.model.response.CustomerInfo;
import net.seckinsen.service.impl.ClientServiceImpl;
import net.seckinsen.util.BaseTestCase;
import net.seckinsen.util.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by seck on 31.08.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest extends BaseTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private RestTemplate restTemplateMock;

    private ClientService clientService;

    private final ClientServiceProperties properties = new ClientServiceProperties("https://sandbox-reporting.rpdpymnt.com/api/v3/client");

    @Before
    public void setUp() {
        clientService = new ClientServiceImpl(restTemplateMock, properties);
    }


    @Test
    public void getClientInformationWithValidTransactionIdAndAuthorizationTokenShouldReturnClientResponse() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "982786-1503662147-3";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        CustomerInfo customerInfo = CustomerInfo.builder()
                .id(689684)
                .email("joe.doe@example.com")
                .birthday("1980-01-01")
                .billingFirstName("John")
                .billingLastName("Doe")
                .billingCity("Antalya")
                .billingCountry("TR")
                .build();

        ClientResponse clientResponse = ClientResponse.builder()
                .customerInfo(customerInfo)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class))
                .thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.OK));

        Optional<ClientResponse> optional = clientService.getClientInformation(clientRequest, authToken);

        // THEN
        verify(restTemplateMock, times(1)).exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
        assertTrue("Fault [expected true]", optional.isPresent());
        assertEquals("Fault [expected 'Customer Path Id' equals]",
                customerInfo.getId(),
                optional.get().getCustomerInfo().getId());
        assertEquals("Fault [expected 'Customer Path Email' equals]",
                customerInfo.getEmail(),
                optional.get().getCustomerInfo().getEmail());
        assertEquals("Fault [expected 'Customer Path Billing City' equals]",
                customerInfo.getBillingCity(),
                optional.get().getCustomerInfo().getBillingCity());
    }

    @Test
    public void getClientInformationWithInvalidAuthorizationTokenShouldThrowUnauthorizedException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "982786-1503662147-3";
        final String expectedExceptionMessage = "401 UNAUTHORIZED";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("HttpClientErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getClientInformationWithInvalidTransactionIdAndValidAuthorizationTokenShouldThrowInternalServerErrorException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "1-1-1";
        final String expectedExceptionMessage = "500 INTERNAL_SERVER_ERROR";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        expectedException.expect(HttpServerErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("HttpServerErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getClientInformationWithVoidTransactionIdAndValidAuthorizationTokenShouldThrowNullCustomerInfoException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "1-1444392550-1";
        final String expectedExceptionMessage = "Customer Information cannot be null";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        ClientResponse clientResponse = ClientResponse.builder()
                .customerInfo(null)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class))
                .thenReturn(new ResponseEntity<>(clientResponse, HttpStatus.OK));
        expectedException.expect(NullCustomerInfoException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("NullCustomerInfoException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getClientInformationWithNotReadableHttpMessageShouldThrowHttpMessageNotReadableException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "1";
        final String expectedExceptionMessage = "Not Readable Http Message";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class))
                .thenThrow(new HttpMessageNotReadableException(expectedExceptionMessage));
        expectedException.expect(HttpMessageNotReadableException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("HttpMessageNotReadableException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getUrl(), HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

}

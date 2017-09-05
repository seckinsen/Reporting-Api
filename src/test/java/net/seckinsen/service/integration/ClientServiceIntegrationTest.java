package net.seckinsen.service.integration;

import net.seckinsen.configuration.properties.UserServiceProperties;
import net.seckinsen.exception.NullCustomerInfoException;
import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;
import net.seckinsen.service.ClientService;
import net.seckinsen.util.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by seck on 31.08.2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServiceIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private ClientService clientService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserServiceProperties properties;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private final String email = "demo@bumin.com.tr";
    private final String password = "cjaiU8CV";


    @Test
    public void getClientInformationWithValidTransactionIdAndAuthorizationTokenShouldReturnClientResponse() throws Exception {
        final String authToken = TestUtils.generateAuthorizationTokenWithValidCredentials(restTemplate, properties.getLogin().getUrl(), email, password);
        final String transactionId = "982786-1503662147-3";

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        Optional<ClientResponse> optional = clientService.getClientInformation(clientRequest, authToken);

        assertTrue("Fault [expected true]", optional.isPresent());
        assertNotNull("Fault [expected 'Customer Path Id' not null]",
                optional.get().getCustomerInfo().getId());
        assertNotNull("Fault [expected 'Customer Path Email' not null]",
                optional.get().getCustomerInfo().getEmail());
        assertNotNull("Fault [expected 'Customer Path Billing City' not null]",
                optional.get().getCustomerInfo().getBillingCity());
    }

    @Test
    public void getClientInformationWithInvalidAuthorizationTokenShouldThrowUnauthorizedException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String transactionId = "982786-1503662147-3";
        final String expectedExceptionMessage = "401 Unauthorized";

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("HttpClientErrorException must be thrown");
        } catch (Exception exp) {
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getClientInformationWithVoidTransactionIdAndValidAuthorizationTokenShouldThrowNullCustomerInfoException() throws Exception {
        final String authToken = TestUtils.generateAuthorizationTokenWithValidCredentials(restTemplate, properties.getLogin().getUrl(), email, password);
        final String transactionId = "1-1444392550-1";
        final String expectedExceptionMessage = "Customer Information cannot be null";

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        expectedException.expect(NullCustomerInfoException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("NullCustomerInfoException must be thrown");
        } catch (Exception exp) {
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getClientInformationWithNotReadableHttpMessageShouldThrowHttpMessageNotReadableException() throws Exception {
        final String authToken = TestUtils.generateAuthorizationTokenWithValidCredentials(restTemplate, properties.getLogin().getUrl(), email, password);
        final String transactionId = "1";

        ClientRequest clientRequest = ClientRequest.builder()
                .transactionId(transactionId)
                .build();

        expectedException.expect(HttpMessageNotReadableException.class);

        try {
            clientService.getClientInformation(clientRequest, authToken);
            fail("HttpMessageNotReadableException must be thrown");
        } catch (Exception exp) {
            throw exp;
        }
    }

}

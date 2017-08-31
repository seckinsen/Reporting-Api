package net.seckinsen.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.seckinsen.model.property.CredentialsProperty;
import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUser;
import net.seckinsen.model.response.MerchantUserInfoResponse;
import net.seckinsen.service.Impl.UserServiceImpl;
import net.seckinsen.util.BaseTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Created by seck on 30.08.2017.
 */

@RunWith(SpringRunner.class)
@RestClientTest(UserServiceImpl.class)
public class UserServiceTest extends BaseTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.login}")
    private String loginPath;

    @Value("${path.merchant.user.info}")
    private String merchantUserInfoPath;

    @Autowired
    private CredentialsProperty credentialsProperty;


    @Test
    public void loginWithInvalidCredentialsShouldReturnEmptyOptionalInstance() throws Exception {
        // GIVEN
        Credentials credentials = Credentials.builder()
                .email(credentialsProperty.getEmail())
                .password(credentialsProperty.getPassword())
                .build();

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + loginPath))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        Optional<AuthToken> tokenOptional = userService.login(credentials);

        // THEN
        mockServer.verify();
        assertFalse("Fault [expected false]", tokenOptional.isPresent());
    }

    @Test
    public void loginWithValidCredentialsShouldReturnToken() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        Credentials credentials = Credentials.builder()
                .email(credentialsProperty.getEmail())
                .password(credentialsProperty.getPassword())
                .build();

        AuthToken token = new AuthToken(authToken);

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + loginPath))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mapper.writeValueAsString(token), MediaType.APPLICATION_JSON));

        Optional<AuthToken> tokenOptional = userService.login(credentials);

        // THEN
        mockServer.verify();
        assertTrue("Fault [expected true]", tokenOptional.isPresent());
        assertEquals("Fault [expected 'AuthToken' equals]", token.getToken(), tokenOptional.get().getToken());
    }

    @Test
    public void getMerchantUserInformationWithInvalidTokenShouldThrowException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String expectedExceptionMessage = "401 Unauthorized";

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(53)
                .build();

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + merchantUserInfoPath))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", authToken))
                .andRespond(withUnauthorizedRequest());
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            userService.getMerchantUserInformation(merchantUserRequest, authToken);
            fail("HttpClientErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            mockServer.verify();
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }

    }

    @Test
    public void getMerchantInformationWithInvalidMerchantUserIdentifierShouldReturnEmptyOptionalInstance() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(1)
                .build();

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + merchantUserInfoPath))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", authToken))
                .andRespond(withServerError());

        Optional<MerchantUserInfoResponse> merchantUserInfoResponseOptional = userService.getMerchantUserInformation(merchantUserRequest, authToken);

        // THEN
        mockServer.verify();
        assertFalse("Fault [expected false]", merchantUserInfoResponseOptional.isPresent());

    }

    @Test
    public void getMerchantInformationWithValidMerchantUserIdentifierAndTokenShouldReturnMerchantUserInformation() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(1)
                .build();

        MerchantUser merchantUser = MerchantUser.builder()
                .id(59)
                .role("admin")
                .email("test@testtest.com")
                .name("Demo User")
                .merchantId(3)
                .secretKey("")
                .build();

        MerchantUserInfoResponse merchantUserInfoResponse = MerchantUserInfoResponse.builder()
                .status("APPROVED")
                .merchantUser(merchantUser)
                .build();

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + merchantUserInfoPath))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", authToken))
                .andRespond(withSuccess(mapper.writeValueAsString(merchantUserInfoResponse), MediaType.APPLICATION_JSON));

        Optional<MerchantUserInfoResponse> merchantUserInfoResponseOptional = userService.getMerchantUserInformation(merchantUserRequest, authToken);

        // THEN
        mockServer.verify();
        assertTrue("Fault [expected true]", merchantUserInfoResponseOptional.isPresent());
        assertEquals("Fault [expected 'Status' equals]",
                merchantUserInfoResponse.getStatus(),
                merchantUserInfoResponseOptional.get().getStatus());
        assertEquals("Fault [expected 'Merchant User Name' equals]",
                merchantUserInfoResponse.getMerchantUser().getName(),
                merchantUserInfoResponseOptional.get().getMerchantUser().getName());
        assertEquals("Fault [expected 'Merchant User Id' equals]",
                merchantUserInfoResponse.getMerchantUser().getId(),
                merchantUserInfoResponseOptional.get().getMerchantUser().getId());

    }

}

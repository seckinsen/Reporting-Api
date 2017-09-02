package net.seckinsen.service;

import net.seckinsen.configuration.properties.Path;
import net.seckinsen.configuration.properties.UserServiceProperties;
import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUser;
import net.seckinsen.model.response.MerchantUserInfoResponse;
import net.seckinsen.service.impl.UserServiceImpl;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by seck on 30.08.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends BaseTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private RestTemplate restTemplateMock;

    private UserService userService;

    private UserServiceProperties properties = new UserServiceProperties(new Path("https://sandbox-reporting.rpdpymnt.com/api/v3/merchant/user/login"), new Path("https://sandbox-reporting.rpdpymnt.com/api/v3/merchant/user/show"));

    @Before
    public void setUp() {
        userService = new UserServiceImpl(restTemplateMock, properties);
    }


    @Test
    public void loginWithValidCredentialsShouldReturnAuthorizationToken() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String email = "demo@demo.com";
        final String password = "lkj123asd";

        Credentials credentials = Credentials.builder()
                .email(email)
                .password(password)
                .build();

        AuthToken token = AuthToken.builder()
                .token(authToken)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getLogin().getUrl(), HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class))
                .thenReturn(new ResponseEntity<>(token, HttpStatus.OK));

        Optional<AuthToken> optional = userService.login(credentials);

        // THEN
        verify(restTemplateMock, times(1)).exchange(properties.getLogin().getUrl(), HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class);
        assertTrue("Fault [expected true]", optional.isPresent());
        assertEquals("Fault [expected 'Authorization Token' equals]", token.getToken(), optional.get().getToken());
    }

    @Test
    public void loginWithInvalidCredentialsShouldThrowInternalServerErrorException() throws Exception {
        // GIVEN
        final String email = "demo@demo.com";
        final String password = "lkj123asd";
        final String expectedExceptionMessage = "500 INTERNAL_SERVER_ERROR";

        Credentials credentials = Credentials.builder()
                .email(email)
                .password(password)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getLogin().getUrl(), HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        expectedException.expect(HttpServerErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            userService.login(credentials);
            fail("HttpServerErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getLogin().getUrl(), HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getMerchantUserInformationWithValidMerchantUserIdentifierAndAuthorizationTokenShouldReturnMerchantUserInformation() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(53)
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
        when(restTemplateMock.exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class))
                .thenReturn(new ResponseEntity<>(merchantUserInfoResponse, HttpStatus.OK));

        Optional<MerchantUserInfoResponse> optional = userService.getMerchantUserInformation(merchantUserRequest, authToken);

        // THEN
        verify(restTemplateMock, times(1)).exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class);
        assertTrue("Fault [expected true]", optional.isPresent());
        assertEquals("Fault [expected 'Status' equals]",
                merchantUserInfoResponse.getStatus(),
                optional.get().getStatus());
        assertEquals("Fault [expected 'Merchant User Name' equals]",
                merchantUserInfoResponse.getMerchantUser().getName(),
                optional.get().getMerchantUser().getName());
        assertEquals("Fault [expected 'Merchant User Id' equals]",
                merchantUserInfoResponse.getMerchantUser().getId(),
                optional.get().getMerchantUser().getId());
    }

    @Test
    public void getMerchantUserInformationWithInvalidTokenShouldThrowUnauthorizedException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String expectedExceptionMessage = "401 UNAUTHORIZED";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(53)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            userService.getMerchantUserInformation(merchantUserRequest, authToken);
            fail("HttpClientErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

    @Test
    public void getMerchantUserInformationWithInvalidMerchantUserIdentifierAndValidAuthorizationTokenShouldThrowInternalServerErrorException() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";
        final String expectedExceptionMessage = "500 INTERNAL_SERVER_ERROR";

        HttpHeaders headers = TestUtils.generateAuthorizationHeader(authToken);

        MerchantUserRequest merchantUserRequest = MerchantUserRequest.builder()
                .id(1)
                .build();

        // WHEN
        when(restTemplateMock.exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        expectedException.expect(HttpServerErrorException.class);
        expectedException.expectMessage(expectedExceptionMessage);

        try {
            userService.getMerchantUserInformation(merchantUserRequest, authToken);
            fail("HttpServerErrorException must be thrown");
        } catch (Exception exp) {
            // THEN
            verify(restTemplateMock, times(1)).exchange(properties.getInfo().getUrl(), HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class);
            assertThat("Fault [expected 'Exception Message' asserts]",
                    exp.getMessage(),
                    is(expectedExceptionMessage));
            throw exp;
        }
    }

}

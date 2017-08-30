package net.seckinsen.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.seckinsen.model.property.CredentialsProperty;
import net.seckinsen.model.request.CredentialsDto;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.service.Impl.UserServiceImpl;
import net.seckinsen.util.BaseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by seck on 30.08.2017.
 */

@RunWith(SpringRunner.class)
@RestClientTest(UserServiceImpl.class)
public class UserServiceTest extends BaseTestCase {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper mapper;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.login}")
    private String path;

    @Autowired
    private CredentialsProperty credentialsProperty;

    @Before
    public void setUp() {
    }

    @Test
    public void loginWithInvalidCredentialsShouldReturnError() throws Exception {
        // GIVEN
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .email(credentialsProperty.getEmail())
                .password(credentialsProperty.getPassword())
                .build();

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + path))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        Optional<AuthToken> tokenOptional = userService.login(credentialsDto);

        // THEN
        assertFalse("Fault [expected false]", tokenOptional.isPresent());
        mockServer.verify();
    }

    @Test
    public void loginWithValidCredentialsShouldReturnToken() throws Exception {
        // GIVEN
        final String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtZXJjaGFudFVzZXJJZCI6NTMsInJvbGUiOiJhZG1pbiIsIm1lcmNoYW50SWQiOjMsInN1Yk1lcmNoYW50SWRzIjpbMyw3NCw5MywxMTkxLDExMSwxMzcsMTM4LDE0MiwxNDUsMTQ2LDE1MywzMzQsMTc1LDE4NCwyMjAsMjIxLDIyMiwyMjMsMjk0LDMyMiwzMjMsMzI3LDMyOSwzMzAsMzQ5LDM5MCwzOTEsNDU1LDQ1Niw0NzksNDg4LDU2MywxMTQ5LDU3MCwxMTM4LDExNTYsMTE1NywxMTU4LDExNzldLCJ0aW1lc3RhbXAiOjE1MDQxMDg3NzN9.Jt5JVXoEEkck4M9fbmDOaykhMpoq-x-D40rY-7Hv_fQ";

        CredentialsDto credentialsDto = CredentialsDto.builder()
                .email(credentialsProperty.getEmail())
                .password(credentialsProperty.getPassword())
                .build();

        AuthToken token = new AuthToken(authToken);

        // WHEN
        mockServer.expect(once(), requestTo(baseUrl + path))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mapper.writeValueAsString(token), MediaType.APPLICATION_JSON));

        Optional<AuthToken> tokenOptional = userService.login(credentialsDto);

        // THEN
        assertTrue("Fault [expected true]", tokenOptional.isPresent());
        assertEquals("Fault [expected 'AuthToken' equals]", token.getToken(), tokenOptional.get().getToken());
        mockServer.verify();
    }

}

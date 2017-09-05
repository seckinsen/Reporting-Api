package net.seckinsen.util;

import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.response.AuthToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * Created by seck on 31.08.2017.
 */

public class TestUtils {

    public static HttpHeaders generateAuthorizationHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        return headers;
    }

    public static String generateAuthorizationTokenWithValidCredentials(RestTemplate restTemplate, String loginUrl, String email, String password) {
        Credentials credentials = Credentials.builder()
                .email(email)
                .password(password)
                .build();

        return restTemplate.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class).getBody().getToken();
    }

}

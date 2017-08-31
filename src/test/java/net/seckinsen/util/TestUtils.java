package net.seckinsen.util;

import org.springframework.http.HttpHeaders;

/**
 * Created by seck on 31.08.2017.
 */

public class TestUtils {

    public static HttpHeaders generateAuthorizationHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        return headers;
    }

}

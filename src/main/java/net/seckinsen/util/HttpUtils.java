package net.seckinsen.util;

import org.springframework.http.HttpHeaders;

/**
 * Created by seck on 01.09.2017.
 */

public final class HttpUtils {

    public HttpUtils() {
        throw new IllegalAccessError("Final Utility Class");
    }

    public static HttpHeaders generateAuthorizationHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        return headers;
    }

}

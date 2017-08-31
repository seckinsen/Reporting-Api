package net.seckinsen.service.Impl;

import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUserInfoResponse;
import net.seckinsen.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Created by seck on 30.08.2017.
 */

@Service
public class UserServiceImpl implements UserService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.login}")
    private String loginPath;

    @Value("${path.merchant.user.info}")
    private String merchantUserInfoPath;

    @Autowired
    public UserServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<AuthToken> login(Credentials credentials) {

        String url = baseUrl + loginPath;
        ResponseEntity<AuthToken> responseEntity;

        try {
            log.info("Login service was called -> {} - ( {} - {} )", url, credentials.getEmail(), credentials.getPassword());
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class);
        } catch (HttpServerErrorException exp) {
            log.error("Api was called wrongly -> status : {} - body : {}", exp.getStatusText(), exp.getResponseBodyAsString());
            return Optional.empty();
        }

        return Optional.of(responseEntity.getBody());

    }

    @Override
    public Optional<MerchantUserInfoResponse> getMerchantUserInformation(MerchantUserRequest merchantUserRequest, String authToken) {

        String url = baseUrl + merchantUserInfoPath;
        ResponseEntity<MerchantUserInfoResponse> responseEntity;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        try {
            log.info("Get merchant user information service was called -> {} - ( id : {} - token : {})", url, merchantUserRequest.getId(), authToken);
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(merchantUserRequest, headers), MerchantUserInfoResponse.class);
        } catch (HttpServerErrorException exp) {
            log.error("Api was called wrongly -> status : {} - body : {}", exp.getStatusText(), exp.getResponseBodyAsString());
            return Optional.empty();
        }

        return Optional.of(responseEntity.getBody());

    }

}

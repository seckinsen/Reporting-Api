package net.seckinsen.service.impl;

import net.seckinsen.configuration.properties.UserServiceProperties;
import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUserInfoResponse;
import net.seckinsen.service.UserService;
import net.seckinsen.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by seck on 30.08.2017.
 */

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    private final String loginUrl;

    private final String infoUrl;

    @Autowired
    public UserServiceImpl(RestTemplate restTemplate, UserServiceProperties properties) {
        this.restTemplate = restTemplate;
        Objects.requireNonNull(properties.getLogin().getUrl(), "Login cannot be null");
        loginUrl = properties.getLogin().getUrl();
        Objects.requireNonNull(properties.getInfo().getUrl(), "Path cannot be null");
        infoUrl = properties.getInfo().getUrl();
    }


    @Override
    public Optional<AuthToken> login(Credentials credentials) {

        log.info("Login service was called -> {} - ( {} - {} )", loginUrl, credentials.getEmail(), credentials.getPassword());

        AuthToken authToken = restTemplate.exchange(loginUrl, HttpMethod.POST, new HttpEntity<>(credentials), AuthToken.class).getBody();

        return Optional.of(authToken);

    }

    @Override
    public Optional<MerchantUserInfoResponse> getMerchantUserInformation(MerchantUserRequest merchantUserRequest, String authToken) {

        log.info("Getting merchant user information service was called -> {} - ( id : {} - token : {})", infoUrl, merchantUserRequest.getId(), authToken);

        MerchantUserInfoResponse merchantUserInfoResponse = restTemplate.exchange(infoUrl, HttpMethod.POST, new HttpEntity<>(merchantUserRequest, HttpUtils.generateAuthorizationHeader(authToken)), MerchantUserInfoResponse.class).getBody();

        return Optional.of(merchantUserInfoResponse);

    }

}

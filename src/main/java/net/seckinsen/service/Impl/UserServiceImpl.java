package net.seckinsen.service.Impl;

import net.seckinsen.model.request.CredentialsDto;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
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
    private String path;

    @Autowired
    public UserServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<AuthToken> login(CredentialsDto credentialsDto) {

        String url = baseUrl + path;
        ResponseEntity<AuthToken> response;

        try {
            log.info("Login service was called -> {} - ( {} - {} )", url, credentialsDto.getEmail(), credentialsDto.getPassword());
            response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(credentialsDto), AuthToken.class);
        } catch (HttpServerErrorException exp) {
            log.error("Api was called wrongly -> status : {} - body : {}", exp.getStatusText(), exp.getResponseBodyAsString());
            return Optional.empty();
        }

        return Optional.of(response.getBody());

    }
}

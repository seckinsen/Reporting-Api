package net.seckinsen.service.impl;

import net.seckinsen.configuration.properties.ClientServiceProperties;
import net.seckinsen.exception.NullCustomerInfoException;
import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;
import net.seckinsen.service.ClientService;
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
 * Created by seck on 31.08.2017.
 */

@Service
public class ClientServiceImpl implements ClientService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    private final String url;

    @Autowired
    public ClientServiceImpl(RestTemplate restTemplate, ClientServiceProperties properties) {
        this.restTemplate = restTemplate;
        Objects.requireNonNull(properties.getUrl(), "Url cannot be null");
        url = properties.getUrl();
    }


    @Override
    public Optional<ClientResponse> getClientInformation(ClientRequest clientRequest, String authToken) throws NullCustomerInfoException {

        log.info("Getting client information service was called -> {} - ( id : {} - token : {})", url, clientRequest.getTransactionId(), authToken);

        ClientResponse clientResponse = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(clientRequest, HttpUtils.generateAuthorizationHeader(authToken)), ClientResponse.class).getBody();

        if (Objects.isNull(clientResponse.getCustomerInfo())) {
            throw new NullCustomerInfoException();
        }

        return Optional.of(clientResponse);

    }

}

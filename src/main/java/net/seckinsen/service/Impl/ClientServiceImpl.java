package net.seckinsen.service.Impl;

import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;
import net.seckinsen.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Created by seck on 31.08.2017.
 */

@Service
public class ClientServiceImpl implements ClientService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${path.client}")
    private String clientPath;

    @Autowired
    public ClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ClientResponse> getClientInformation(ClientRequest clientRequest, String authToken) {

        String url = baseUrl + clientPath;
        ResponseEntity<ClientResponse> responseEntity;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        try {
            log.info("Get client information service was called -> {} - ( id : {} - token : {})", url, clientRequest.getTransactionId(), authToken);
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(clientRequest, headers), ClientResponse.class);
            Optional.of(responseEntity.getBody().getCustomerInfo()).orElseThrow(NullPointerException::new);
        } catch (HttpServerErrorException exp) {
            log.error("Api was called wrongly -> status : {} - body : {}", exp.getStatusText(), exp.getResponseBodyAsString());
            return Optional.empty();
        } catch (HttpMessageNotReadableException | NullPointerException exp) {
            log.error("Api was called wrongly -> message : {}", exp.getMessage());
            return Optional.empty();
        }

        return Optional.of(responseEntity.getBody());

    }

}

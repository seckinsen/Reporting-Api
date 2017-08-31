package net.seckinsen.controller;

import net.seckinsen.model.error.ApiError;
import net.seckinsen.model.error.AuthorizationError;
import net.seckinsen.model.error.ErrorResponse;
import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;
import net.seckinsen.service.ClientService;
import net.seckinsen.util.ErrorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by seck on 31.08.2017.
 */

@RestController
public class ClientController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = "application/json; charset=UTF-8")
    public ResponseEntity getClientInformation(@RequestHeader(value = "Authorization", required = false) String authToken,
                                               @RequestBody @Valid ClientRequest clientRequest,
                                               BindingResult bindingResult) {

        log.info("Client information request attempt -> ( Transaction id : {} ) - Authorization ( {} )", clientRequest.getTransactionId(), authToken);

        if (authToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Missed!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(ErrorUtils.getBindingResultErrors(bindingResult)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Optional<ClientResponse> clientResponseOptional = clientService.getClientInformation(clientRequest, authToken);

            if (!clientResponseOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponse(Stream.of(new ApiError()).collect(Collectors.toList())), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(clientResponseOptional.get(), HttpStatus.OK);
        } catch (HttpClientErrorException exp) {
            log.error("Api was called wrongly -> status : {} - message : {}", exp.getStatusText(), exp.getMessage());
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Expired!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }

    }

}

package net.seckinsen.controller;

import net.seckinsen.model.error.AuthorizationError;
import net.seckinsen.model.error.ErrorResponse;
import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by seck on 31.08.2017.
 */

@RestController
public class ClientController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ClientService clientService;

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
            return new ResponseEntity<>(ErrorResponse.create(new AuthorizationError("Token Missed!")), HttpStatus.UNAUTHORIZED);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorResponse.create(bindingResult), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return clientService.getClientInformation(clientRequest, authToken)
                .map(clientResponse -> new ResponseEntity<>(clientResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(ErrorResponse.create(), HttpStatus.INTERNAL_SERVER_ERROR));

    }

}

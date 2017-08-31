package net.seckinsen.controller;

import net.seckinsen.model.error.ApiError;
import net.seckinsen.model.error.AuthorizationError;
import net.seckinsen.model.error.ErrorResponse;
import net.seckinsen.model.error.LoginError;
import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUserInfoResponse;
import net.seckinsen.service.UserService;
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
 * Created by seck on 30.08.2017.
 */

@RestController
public class UserController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/login", produces = "application/json; charset=UTF-8")
    public ResponseEntity userLogin(@RequestBody @Valid Credentials credentials,
                                    BindingResult bindingResult) {

        log.info("User login attempt -> Credential ( email : {} - password : {} )", credentials.getEmail(), credentials.getPassword());

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(ErrorUtils.getBindingResultErrors(bindingResult)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<AuthToken> tokenOptional = userService.login(credentials);

        if (!tokenOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new LoginError()).collect(Collectors.toList())), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(tokenOptional.get(), HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/show", produces = "application/json; charset=UTF-8")
    public ResponseEntity getMerchantUserInformation(@RequestHeader(value = "Authorization", required = false) String authToken,
                                  @RequestBody @Valid MerchantUserRequest merchantUserRequest,
                                  BindingResult bindingResult) {

        log.info("Merchant user request attempt -> ( User id : {} ) - Authorization ( {} )", merchantUserRequest.getId(), authToken);

        if (authToken.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Missed!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ErrorResponse(ErrorUtils.getBindingResultErrors(bindingResult)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Optional<MerchantUserInfoResponse> merchantInfoResponseOptional = userService.getMerchantUserInformation(merchantUserRequest, authToken);

            if (!merchantInfoResponseOptional.isPresent()) {
                return new ResponseEntity<>(new ErrorResponse(Stream.of(new ApiError()).collect(Collectors.toList())), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(merchantInfoResponseOptional.get(), HttpStatus.OK);
        } catch (HttpClientErrorException exp) {
            log.error("Api was called wrongly -> status : {} - message : {}", exp.getStatusText(), exp.getMessage());
            return new ResponseEntity<>(new ErrorResponse(Stream.of(new AuthorizationError("Token Expired!")).collect(Collectors.toList())), HttpStatus.UNAUTHORIZED);
        }


    }

}

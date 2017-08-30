package net.seckinsen.controller;

import net.seckinsen.model.error.LoginError;
import net.seckinsen.model.request.CredentialDto;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.service.UserService;
import net.seckinsen.util.ErrorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

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
    public ResponseEntity userLogin(@RequestBody @Valid CredentialDto credentialDto,
                                    BindingResult bindingResult) {

        log.info("User login attempt -> Credential ( email : {} - password : {} )", credentialDto.getEmail(), credentialDto.getPassword());

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorUtils.getBindingResultErrors(bindingResult), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<AuthToken> tokenOptional = userService.login(credentialDto);

        if (!tokenOptional.isPresent()) {
            return new ResponseEntity<>(new LoginError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(tokenOptional.get(), HttpStatus.OK);

    }

}

package net.seckinsen.service;

import net.seckinsen.model.request.CredentialsDto;
import net.seckinsen.model.response.AuthToken;

import java.util.Optional;

/**
 * Created by seck on 30.08.2017.
 */

public interface UserService {

    Optional<AuthToken> login(CredentialsDto credentialsDto);

}

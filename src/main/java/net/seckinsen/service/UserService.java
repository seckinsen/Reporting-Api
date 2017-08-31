package net.seckinsen.service;

import net.seckinsen.model.request.Credentials;
import net.seckinsen.model.request.MerchantUserRequest;
import net.seckinsen.model.response.AuthToken;
import net.seckinsen.model.response.MerchantUserInfoResponse;

import java.util.Optional;

/**
 * Created by seck on 30.08.2017.
 */

public interface UserService {

    Optional<AuthToken> login(Credentials credentials);

    Optional<MerchantUserInfoResponse> getMerchantUserInformation(MerchantUserRequest merchantUserRequest, String authToken);


}

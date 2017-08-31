package net.seckinsen.model.error;

/**
 * Created by seck on 31.08.2017.
 */

public class AuthorizationError extends BaseError {

    public AuthorizationError() {
        super("Authorization Failed!");
    }

    public AuthorizationError(String message) {
        super(message);
    }

}

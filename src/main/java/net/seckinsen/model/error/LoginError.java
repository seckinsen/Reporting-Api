package net.seckinsen.model.error;

/**
 * Created by seck on 30.08.2017.
 */

public class LoginError extends BaseError {

    public LoginError() {
        super("Invalid Credentials!");
    }

    public LoginError(String message) {
        super(message);
    }

}

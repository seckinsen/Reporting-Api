package net.seckinsen.model.error;

/**
 * Created by seck on 31.08.2017.
 */

public class ApiError extends BaseError {

    public ApiError() {
        super("Invalid Request!");
    }

    public ApiError(String message) {
        super(message);
    }

}

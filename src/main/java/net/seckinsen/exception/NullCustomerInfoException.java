package net.seckinsen.exception;

/**
 * Created by seck on 01.09.2017.
 */
public class NullCustomerInfoException extends RuntimeException {

    public NullCustomerInfoException() {
        super("Customer Information cannot be null");
    }

}
package net.seckinsen.exception.handler;

import net.seckinsen.model.error.ApiError;
import net.seckinsen.model.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by seck on 01.09.2017.
 */

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(Exception exp) {
        return new ResponseEntity<>(new ErrorResponse(Stream.of(new ApiError(exp.getCause().getMessage().split("\n")[0])).collect(Collectors.toList())), HttpStatus.BAD_REQUEST);
    }

}

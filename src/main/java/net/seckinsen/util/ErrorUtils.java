package net.seckinsen.util;

import net.seckinsen.model.error.BaseError;
import net.seckinsen.model.error.BindingError;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by seck on 30.08.2017.
 */

public final class ErrorUtils {

    public ErrorUtils() {
        throw new IllegalAccessError("Final Utility Class");
    }

    public static List<BaseError> getBindingResultErrors(BindingResult bindingResult) {

        return bindingResult.getFieldErrors()
                .stream()
                .map(error -> new BindingError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

    }

}

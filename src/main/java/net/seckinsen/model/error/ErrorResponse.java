package net.seckinsen.model.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.seckinsen.util.ErrorUtils;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private List<BaseError> errors;

    public static ErrorResponse create() {
        return ErrorResponse.create(new ApiError());
    }

    public static ErrorResponse create(BaseError error) {
        return new ErrorResponse(Collections.singletonList(error));
    }

    public static ErrorResponse create(List<BaseError> error) {
        return new ErrorResponse(error);
    }

    public static ErrorResponse create(BindingResult bindingResult) {
        return new ErrorResponse(ErrorUtils.getBindingResultErrors(bindingResult));
    }

}

package net.seckinsen.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by seck on 31.08.2017.
 */

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private List<BaseError> errors;

}

package net.seckinsen.model.error;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by seck on 30.08.2017.
 */

@Getter
@Setter
public class BindingError extends BaseError {

    private String field;

    public BindingError(String field, String message) {
        super(message);
        this.field = field;
    }

}

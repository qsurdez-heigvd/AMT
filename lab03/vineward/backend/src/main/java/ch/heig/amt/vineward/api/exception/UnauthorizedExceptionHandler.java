package ch.heig.amt.vineward.api.exception;

import ch.aegis.exception.UnauthorizedActionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Simple handler showing the justification when an action is forbidden.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@ControllerAdvice
public class UnauthorizedExceptionHandler {

    @ExceptionHandler({UnauthorizedActionException.class})
    public ResponseEntity<String> handleUnauthorizedActionException(
        UnauthorizedActionException e
    ) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(e.getMessage());
    }
}

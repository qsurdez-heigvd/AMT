package ch.heig.amt.vineward.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to register with a username that already exists.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
        super("The provided username already exists.");
    }
}

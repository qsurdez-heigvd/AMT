package ch.heig.amt.vineward.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user tries to access a protected resource without being authenticated.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException() {
        super("User is not authenticated.");
    }
}

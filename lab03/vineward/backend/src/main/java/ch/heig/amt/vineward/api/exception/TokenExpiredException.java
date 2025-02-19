package ch.heig.amt.vineward.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a token is expired.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("The provided token is expired.");
    }
}

package ch.heig.amt.vineward.api.exception;

import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic exception allowing to signal that a given object was not found, allows to build itself
 * using a list of standard reason for a uniform response message format.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends RuntimeException {

    private final Class<?> objectType;

    private ObjectNotFoundException(
        Class<?> objectType,
        String message
    ) {
        super(message);
        this.objectType = objectType;
    }

    public static IdentifierBuilder forObject(Class<?> objectType) {
        return new IdentifierBuilder(objectType);
    }

    public static IdentifierBuilder forReview() {
        return new IdentifierBuilder(Review.class);
    }

    public static IdentifierBuilder forUser() {
        return new IdentifierBuilder(User.class);
    }

    public static IdentifierBuilder forWine() {
        return new IdentifierBuilder(Wine.class);
    }

    public static class IdentifierBuilder {

        private final Class<?> objectType;
        private final StringBuilder message;

        private IdentifierBuilder(Class<?> objectType) {
            this.objectType = objectType;
            this.message = new StringBuilder()
                .append("Could not find [")
                .append(objectType.getSimpleName())
                .append("]");
        }

        /**
         * Appends a specific reason to the message, prefer standard methods when possible.
         *
         * @param reason the reason to append
         * @return the builder
         */
        private IdentifierBuilder append(String reason) {
            message.append(reason);
            return this;
        }

        /**
         * Specifies that the object was not found by searching for an ID.
         *
         * @param id the ID that was searched
         * @return the exception
         */
        public ObjectNotFoundException fromId(Long id) {
            message.append(" from ID [").append(id).append("]");
            return build();
        }

        /**
         * Specifies that the object was not found by searching for an email.
         *
         * @param email the email that was searched
         * @return the exception
         */
        public ObjectNotFoundException fromEmail(String email) {
            message.append(" from email [").append(email).append("]");
            return build();
        }

        public ObjectNotFoundException build() {
            return new ObjectNotFoundException(objectType, message.toString());
        }
    }

}

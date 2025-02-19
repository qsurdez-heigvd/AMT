package ch.heig.amt.vineward.security;

import ch.heig.amt.vineward.api.exception.UserNotAuthenticatedException;
import ch.heig.amt.vineward.business.model.user.User;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper method to get the authenticated user and additional data from the security context.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public interface UserSecurityGetter {

    static User getAuthenticatedUser() {
        return getAuthentication()
            .map(Authentication::getPrincipal)
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .orElseThrow(UserNotAuthenticatedException::new);
    }

    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
}

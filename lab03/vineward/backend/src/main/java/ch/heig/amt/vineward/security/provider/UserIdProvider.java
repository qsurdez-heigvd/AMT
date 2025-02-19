package ch.heig.amt.vineward.security.provider;

import ch.aegis.contract.AttributeProvider;
import ch.heig.amt.vineward.security.UserSecurityGetter;
import ch.heig.amt.vineward.security.attribute.UserIdAttribute;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Provider for the user's canton of origin, fetches it from the {@link SecurityContextHolder}.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Component
public final class UserIdProvider implements AttributeProvider<UserIdAttribute> {

    @Override
    public Class<UserIdAttribute> getAttributeType() {
        return UserIdAttribute.class;
    }

    @Override
    public UserIdAttribute provide() {
        return new UserIdAttribute(UserSecurityGetter.getAuthenticatedUser().getId());
    }
}

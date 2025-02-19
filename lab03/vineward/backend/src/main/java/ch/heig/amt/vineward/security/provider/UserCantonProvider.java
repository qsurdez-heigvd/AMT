package ch.heig.amt.vineward.security.provider;

import ch.aegis.contract.AttributeProvider;
import ch.heig.amt.vineward.security.UserSecurityGetter;
import ch.heig.amt.vineward.security.attribute.UserCantonAttribute;
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
public final class UserCantonProvider implements AttributeProvider<UserCantonAttribute> {

    @Override
    public Class<UserCantonAttribute> getAttributeType() {
        return UserCantonAttribute.class;
    }

    @Override
    public UserCantonAttribute provide() {
        return new UserCantonAttribute(UserSecurityGetter.getAuthenticatedUser().getOrigin());
    }
}

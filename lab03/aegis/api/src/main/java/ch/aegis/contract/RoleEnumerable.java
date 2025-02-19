package ch.aegis.contract;

import ch.aegis.model.Attribute;
import org.springframework.security.core.GrantedAuthority;

/**
 * Base contract for role enumerations, to be implemented manually.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface RoleEnumerable<E extends Enum<E> & RoleEnumerable<E>>
    extends Attribute, GrantedAuthority {

    /**
     * Returns the display name of the role.
     *
     * @return the display name of the role
     */
    String getDisplayName();

    /**
     * Returns the enum value.
     *
     * @return the enum value
     */
    @SuppressWarnings("unchecked")
    default E getEnumValue() {
        return (E) this;
    }

    /**
     * Returns a granted authority for the role.
     *
     * @return a granted authority for the role
     */
    @Override
    default String getAuthority() {
        return "ROLE_%s".formatted(getEnumValue().name());
    }
}

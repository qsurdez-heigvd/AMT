package ch.heig.amt.vineward.security.attribute;

import ch.aegis.model.support.EnumeratedAttribute;
import ch.heig.amt.vineward.business.model.Canton;

/**
 * Attribute representing a user's canton of origin.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public final class UserCantonAttribute extends EnumeratedAttribute<Canton> {

    public UserCantonAttribute(Canton value) {
        super(value);
    }
}

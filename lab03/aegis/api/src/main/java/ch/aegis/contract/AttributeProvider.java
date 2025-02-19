package ch.aegis.contract;

import ch.aegis.model.Attribute;

/**
 * Base contract for attribute providers, they are responsible for dynamically providing attributes
 * which cannot be converted from a front-facing argument. A common example would be when providing
 * a user attribute from the security context holder. Note that Aegis already resolves the role for
 * you, so you do not need to implement this contract for user roles.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface AttributeProvider<A extends Attribute> {

    /**
     * Returns the type of the attribute that will be provided.
     *
     * @return the type of the attribute that will be provided
     */
    Class<A> getAttributeType();

    /**
     * Provides the attribute.
     *
     * @return the attribute
     */
    A provide();
}

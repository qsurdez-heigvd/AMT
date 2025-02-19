package ch.aegis.contract;

import ch.aegis.model.Attribute;

/**
 * Base contract for attribute resolvers, they are responsible for providing attributes given a
 * source attribute. A common example would be to resolve an attribute from a database row given an
 * ID given in the endpoint. It is highly recommended to make use of mapstruct when creating
 * mappings between a database entry and an attribute.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface AttributeResolver<S extends Attribute, R extends Attribute> {

    /**
     * Returns the type of the source attribute that will be resolved.
     *
     * @return the type of the source attribute that will be resolved
     */
    Class<S> getSourceAttributeType();

    /**
     * Returns the type of the resolved attribute.
     *
     * @return the type of the resolved attribute
     */
    Class<R> getResolvedAttributeType();

    /**
     * Resolves the attribute.
     *
     * @param sourceAttribute the source attribute
     * @return the resolved attribute
     */
    R resolve(S sourceAttribute);
}

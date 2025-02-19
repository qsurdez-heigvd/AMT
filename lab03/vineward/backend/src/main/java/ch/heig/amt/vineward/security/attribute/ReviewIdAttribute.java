package ch.heig.amt.vineward.security.attribute;


import ch.aegis.model.support.LongAttribute;

/**
 * Wrapper type representing a wine by its ID.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public final class ReviewIdAttribute extends LongAttribute {

    public ReviewIdAttribute(Long value) {
        super(value);
    }

    public ReviewIdAttribute(String value) {
        super(value);
    }
}

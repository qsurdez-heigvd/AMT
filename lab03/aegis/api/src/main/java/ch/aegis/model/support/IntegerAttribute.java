package ch.aegis.model.support;

import ch.aegis.model.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Support implementation of an attribute with an integer value, specific classes should be created
 * based on this abstract implementation for disambiguation within the provider system.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class IntegerAttribute implements Attribute {

    protected final Integer value;

    protected IntegerAttribute(Integer value) {
        this.value = value;
    }

    protected IntegerAttribute(String value) {
        this(Integer.valueOf(value));
    }
}

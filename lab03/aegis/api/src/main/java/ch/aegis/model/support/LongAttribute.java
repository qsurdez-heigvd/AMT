package ch.aegis.model.support;

import ch.aegis.model.Attribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Support implementation of an attribute with a long value, specific classes should be created
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
public abstract class LongAttribute implements Attribute {

    protected final Long value;

    protected LongAttribute(Long value) {
        this.value = value;
    }

    protected LongAttribute(String value) {
        this(Long.valueOf(value));
    }
}

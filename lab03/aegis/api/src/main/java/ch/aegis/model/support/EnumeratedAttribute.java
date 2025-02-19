package ch.aegis.model.support;

import ch.aegis.model.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Support implementation of an attribute with an enum value, specific classes should be created
 * based on this abstract implementation for disambiguation within the provider system.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@ToString
@EqualsAndHashCode
public abstract class EnumeratedAttribute<E extends Enum<E>> implements Attribute {

    protected final E value;
    protected final Class<E> enumClass;

    @SuppressWarnings("unchecked")
    protected EnumeratedAttribute(E value) {
        this.value = value;
        this.enumClass = (Class<E>) value.getClass();
    }

    public final E value() {
        return value;
    }
}

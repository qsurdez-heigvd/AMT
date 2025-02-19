package ch.aegis.model;

/**
 * An attribute is a generic value that can be obtained from the referenced resources when making an
 * access control decision. It can be a user attribute, a resource attribute, or an environment
 * attribute. Attributes can be used in policies to define the conditions under which a policy is
 * applicable. Attributes can be modeled by records, {@link lombok.Data} classes, or any other type
 * wrapper, for the latter some support classes are available.
 * <p>
 * Attributes shall be available through either a {@link ch.aegis.contract.AttributeProvider} or a
 * {@link ch.aegis.contract.AttributeResolver} to be used in policies.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface Attribute {
}

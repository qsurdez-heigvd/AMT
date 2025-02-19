package ch.aegis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be applied to an enum implementing {@link ch.aegis.contract.RoleEnumerable} to indicate
 * that the enum represents the roles that can be assigned to users, it will be used in generated
 * classes and annotations for access-control usage.
 * <p>
 * The annotated role enum will thus become an attribute available in the access control policies.
 * <p>
 * If a user has multiple authorities mapping to the annoted role definition, the one with the
 * <b>lowest ordinal</b> value will be selected first for the policies. This means roles have a
 * default top-down hierarchy as defined by the order of roles in the enum declaration.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RoleDefinition {
}

package ch.aegis.annotation;

import ch.aegis.PolicyEffect;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that the class on which this annotation is used is meant to represent a list of
 * policies for a specific action. The class is supposed to only contain matcher methods taking in
 * arguments and returning a {@link ch.aegis.PolicyResult}.
 * <p>
 * The class should contain at least one method annotated with {@link ActionPolicy}. Methods should
 * take in arguments that are providable through the {@link ch.aegis.model.Attribute} system.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ActionDefinition {

    /**
     * @return how the policies should be evaluated for the action, defaults to
     * {@link PolicyEffect#REQUIRE_ANY}.
     */
    PolicyEffect effect() default PolicyEffect.REQUIRE_ANY;
}

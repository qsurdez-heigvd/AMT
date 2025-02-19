package ch.aegis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method is a policy for the enclosing {@link ActionDefinition}
 * class.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ActionPolicy {

    /**
     * A custom name for the policy. If not provided, the name of the method will be used.
     *
     * @return the name of the policy, defaults to the name of the method.
     */
    String name() default "";

    /**
     * A description for the policy.
     *
     * @return the description of the policy.
     */
    String description() default "";
}

package ch.aegis.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the test package as needing to use Aegis Spring Security test helpers. When present, the
 * test annotation processor will generate the necessary test helpers for creating tests using the
 * policy definitions.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
public @interface EnableAegisSpringSecurityTesting {
}

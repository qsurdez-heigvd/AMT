package ch.aegis.processor.internal.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for naming conversions and manipulations.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@UtilityClass
public final class NamingUtil {

    /**
     * Converts a camelCase string to a SCREAMING_SNAKE_CASE string.
     *
     * @param camelCase the camelCase string to convert
     * @return the SCREAMING_SNAKE_CASE string
     */
    public static String convertCamelCaseToScreamingSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }
}

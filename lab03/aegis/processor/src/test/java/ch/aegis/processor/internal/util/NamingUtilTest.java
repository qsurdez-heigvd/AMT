package ch.aegis.processor.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NamingUtilTest {

    @ParameterizedTest
    @CsvSource({
        "camelCase,CAMEL_CASE",
        "CreateWineReview,CREATE_WINE_REVIEW",
    })
    void testCamelCaseToScreamingSnakeCase(String camelCase, String expected) {
        assertEquals(expected, NamingUtil.convertCamelCaseToScreamingSnakeCase(camelCase));
    }
}

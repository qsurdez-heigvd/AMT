package ch.aegis.processor.internal.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.aegis.processor.PolicyProcessor;
import com.palantir.javapoet.AnnotationSpec;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GeneratorUtilsTest {

    @Nested
    class GeneratedAnnotationTests {

        @Test
        void basicAnnotationWithoutComments_ShouldHaveRequiredFields() {
            AnnotationSpec annotation = GeneratorUtils.createGeneratedAnnotation();

            assertNotNull(annotation);
            String annotationString = annotation.toString();
            assertTrue(annotationString.contains("@javax.annotation.processing.Generated("));
            assertTrue(annotationString.contains("value = \"" + PolicyProcessor.class.getName() + "\""));
            assertTrue(annotationString.contains("date = \""));
        }

        @Test
        void annotationWithComments_ShouldIncludeComments() {
            String[] comments = {"Test comment 1", "Test comment 2"};

            AnnotationSpec annotation = GeneratorUtils.createGeneratedAnnotation(comments);

            assertNotNull(annotation);
            String annotationString = annotation.toString();
            assertTrue(annotationString.contains("comments = \"Test comment 1, Test comment 2\""));
        }

        @Nested
        class EdgeCases {

            @Test
            void emptyComments_ShouldNotIncludeCommentsField() {
                AnnotationSpec annotation = GeneratorUtils.createGeneratedAnnotation(new String[0]);

                assertNotNull(annotation);
                String annotationString = annotation.toString();
                assertFalse(annotationString.contains("comments"));
            }

            @Test
            void nullComments_ShouldNotIncludeCommentsField() {
                AnnotationSpec annotation = GeneratorUtils.createGeneratedAnnotation((String[]) null);

                assertNotNull(annotation);
                String annotationString = annotation.toString();
                assertFalse(annotationString.contains("comments"));
            }
        }
    }

    @Nested
    class IntellijLanguageAnnotationTests {

        @Test
        void validLanguage_ShouldCreateProperAnnotation() {
            String language = "SQL";

            AnnotationSpec annotation = GeneratorUtils.createIntellijLanguageAnnotation(language);

            assertNotNull(annotation);
            String annotationString = annotation.toString();
            assertEquals("@org.intellij.lang.annotations.Language(\"SQL\")", annotationString);
        }

        @Test
        void emptyLanguage_ShouldCreateAnnotationWithEmptyValue() {
            String language = "";

            AnnotationSpec annotation = GeneratorUtils.createIntellijLanguageAnnotation(language);

            assertNotNull(annotation);
            String annotationString = annotation.toString();
            assertEquals("@org.intellij.lang.annotations.Language(\"\")", annotationString);
        }
    }
}

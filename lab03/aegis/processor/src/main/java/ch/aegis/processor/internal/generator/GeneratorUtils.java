package ch.aegis.processor.internal.generator;

import ch.aegis.processor.PolicyProcessor;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import java.time.Instant;
import javax.annotation.processing.Generated;
import lombok.experimental.UtilityClass;

/**
 * Utility methods for JavaPoet code generation.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@UtilityClass
public final class GeneratorUtils {

    /**
     * Creates a JavaPoet AnnotationSpec for the @Generated annotation.
     *
     * @param comments Optional comments to include in the annotation
     * @return AnnotationSpec for @Generated annotation
     */
    public static AnnotationSpec createGeneratedAnnotation(String... comments) {
        AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", PolicyProcessor.class.getName())
            .addMember("date", "$S", Instant.now().toString());

        if (comments != null && comments.length > 0) {
            annotationBuilder.addMember("comments", "$S", String.join(", ", comments));
        }

        return annotationBuilder.build();
    }

    /**
     * Creates a JavaPoet AnnotationSpec for the @Language annotation from IntelliJ annotations.
     *
     * @param language Language value for the annotation
     * @return AnnotationSpec for @Language annotation
     */
    public static AnnotationSpec createIntellijLanguageAnnotation(String language) {
        return AnnotationSpec.builder(ClassName.get("org.intellij.lang.annotations", "Language"))
            .addMember("value", "$S", language)
            .build();
    }
}

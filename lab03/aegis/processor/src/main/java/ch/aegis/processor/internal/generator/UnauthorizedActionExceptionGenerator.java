package ch.aegis.processor.internal.generator;

import ch.aegis.processor.internal.util.AnnotationProcessorContext;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import javax.lang.model.element.Modifier;

/**
 * Generates the UnauthorizedActionException class used by Aegis to signal unauthorized access
 * attempts to protected actions.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public final class UnauthorizedActionExceptionGenerator {

    private static final String GENERATED_CLASS_NAME = "UnauthorizedActionException";
    private static final String GENERATED_PACKAGE = "ch.aegis.exception";

    private final AnnotationProcessorContext context;

    private UnauthorizedActionExceptionGenerator(AnnotationProcessorContext context) {
        this.context = context;
    }

    /**
     * Generates the UnauthorizedActionException class file. This exception is thrown when a user
     * attempts to perform an action they are not authorized for.
     *
     * @throws IOException if the file cannot be written
     */
    public void generate() throws IOException {
        var classBuilder = createClassBuilder();
        addFields(classBuilder);
        addConstructor(classBuilder);

        var javaFile = JavaFile.builder(GENERATED_PACKAGE, classBuilder.build())
            .skipJavaLangImports(true)
            .build();

        javaFile.writeTo(context.getFiler());
    }

    /**
     * Creates the base class builder with proper superclass and annotations.
     *
     * @return the initialized TypeSpec.Builder
     */
    private TypeSpec.Builder createClassBuilder() {
        return TypeSpec.classBuilder(GENERATED_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC)
            .superclass(RuntimeException.class)
            .addAnnotation(ClassName.get("lombok", "Getter"))
            .addAnnotation(
                AnnotationSpec.builder(
                    ClassName.get("org.springframework.web.bind.annotation", "ResponseStatus")
                )
                    .addMember(
                        "value",
                        "$T.FORBIDDEN",
                        ClassName.get("org.springframework.http", "HttpStatus")
                    )
                    .build()
            )
            .addAnnotation(GeneratorUtils.createGeneratedAnnotation())
            .addJavadoc(
                """
                Exception thrown when a user attempts to perform an action they are not authorized for.
                Contains details about the attempted action and the reason for denial.
                """
            );
    }

    /**
     * Adds the required fields to the class.
     *
     * @param classBuilder the class builder to add fields to
     */
    private void addFields(TypeSpec.Builder classBuilder) {
        classBuilder.addField(
            FieldSpec.builder(
                ClassName.get("ch.aegis.security", "AegisAction"),
                "action",
                Modifier.PRIVATE,
                Modifier.FINAL
            )
                .addJavadoc("The action that was attempted but not authorized.\n")
                .build()
        );

        classBuilder.addField(
            FieldSpec.builder(String.class, "justification", Modifier.PRIVATE, Modifier.FINAL)
                .addJavadoc("The reason why the action was not authorized.\n")
                .build()
        );
    }

    private void addConstructor(TypeSpec.Builder classBuilder) {
        classBuilder.addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("ch.aegis.security", "AegisAction"), "action")
                .addParameter(String.class, "justification")
                .addStatement(
                    "super($S.formatted(action, justification))",
                    "Unauthorized action: %s%nJustification: %s"
                )
                .addStatement("this.action = action")
                .addStatement("this.justification = justification")
                .addJavadoc(
                    """
                    Creates a new UnauthorizedActionException.

                    @param action the action that was attempted but not authorized
                    @param justification the reason why the action was not authorized
                    """
                )
                .build()
        );
    }

    /**
     * Creates a new instance of UnauthorizedActionExceptionGenerator from the given context.
     *
     * @param context the annotation processor context
     * @return a new UnauthorizedActionExceptionGenerator instance
     */
    public static UnauthorizedActionExceptionGenerator fromContext(
        AnnotationProcessorContext context
    ) {
        return new UnauthorizedActionExceptionGenerator(context);
    }
}

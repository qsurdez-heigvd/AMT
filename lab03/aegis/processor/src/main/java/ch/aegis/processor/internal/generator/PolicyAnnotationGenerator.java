package ch.aegis.processor.internal.generator;

import ch.aegis.processor.internal.util.ActionDefinitionContext;
import ch.aegis.processor.internal.util.AnnotationProcessorContext;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Generates authorization annotation interfaces for every policy, meant to be added on endpoints
 * to enforce authorization rules using the generated interceptors. The annotations will include
 * SpEL expression parameters for each required attribute, allowing for attribute overrides whenever
 * necessary.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public final class PolicyAnnotationGenerator {

    private final AnnotationProcessorContext context;
    private final ActionDefinitionContext actionContext;

    private PolicyAnnotationGenerator(
        AnnotationProcessorContext context,
        ActionDefinitionContext actionContext
    ) {
        this.context = context;
        this.actionContext = actionContext;
    }

    /**
     * Generates the authorization annotation class for a specific policy. The annotation will
     * include SpEL expression parameters for each required attribute.
     *
     * @throws IOException if the file cannot be written
     */
    public void generate() throws IOException {
        String annotationName = buildAnnotationName();
        TypeSpec.Builder annotationBuilder = createAnnotationBuilder(annotationName);
        addAttributeParameters(annotationBuilder);

        JavaFile javaFile = JavaFile.builder(getTargetPackage(), annotationBuilder.build())
            .skipJavaLangImports(true)
            .build();

        javaFile.writeTo(context.getFiler());
    }

    private TypeSpec.Builder createAnnotationBuilder(String annotationName) {
        return TypeSpec.annotationBuilder(annotationName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(Target.class)
                    .addMember("value", "$T.METHOD", ElementType.class)
                    .build()
            )
            .addAnnotation(
                AnnotationSpec.builder(Retention.class)
                    .addMember("value", "$T.RUNTIME", RetentionPolicy.class)
                    .build()
            )
            .addAnnotation(Documented.class)
            .addAnnotation(GeneratorUtils.createGeneratedAnnotation())
            .addJavadoc(buildJavadoc());
    }

    private String buildAnnotationName() {
        String actionName = actionContext.actionClass().getSimpleName().toString();
        return "Authorize" + actionName;
    }

    private void addAttributeParameters(TypeSpec.Builder annotationBuilder) {
        Set<TypeElement> attributes = actionContext
            .policies()
            .stream()
            .flatMap(policy -> policy.requiredAttributes().stream())
            .collect(Collectors.toSet());

        for (TypeElement attribute : attributes) {
            String parameterName = buildParameterName(attribute);
            MethodSpec parameter = MethodSpec.methodBuilder(parameterName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(String.class)
                .defaultValue("\"\"") // Empty string as default value
                .addAnnotation(GeneratorUtils.createIntellijLanguageAnnotation("SpEL"))
                .addJavadoc(
                    "SpEL expression to override the $L attribute given to action policies.\n",
                    attribute.getSimpleName()
                )
                .addJavadoc(
                    "@return the SpEL expression for the attribute override, leave blank to keep the default attribute provider detection.\n"
                )
                .build();

            annotationBuilder.addMethod(parameter);
        }
    }

    private String buildParameterName(TypeElement attribute) {
        String name = attribute.getSimpleName().toString();
        // Remove "Attribute" suffix if present
        if (name.endsWith("Attribute")) {
            name = name.substring(0, name.length() - "Attribute".length());
        }
        // Convert first character to lowercase
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private String buildJavadoc() {
        StringBuilder doc = new StringBuilder();
        doc
            .append("Authorization annotation for the {@link ")
            .append(actionContext.actionClass().getQualifiedName())
            .append("} action.\n\n");

        doc.append("@generated by PolicyAnnotationGenerator\n");
        return doc.toString();
    }

    private String getTargetPackage() {
        return context
            .getElements()
            .getPackageOf(actionContext.actionClass())
            .getQualifiedName()
            .toString();
    }

    /**
     * Creates a new instance of PolicyAnnotationGenerator.
     *
     * @param context          the annotation processor context
     * @param actionContext    the action definition context
     * @return a new PolicyAnnotationGenerator instance
     */
    public static PolicyAnnotationGenerator of(
        AnnotationProcessorContext context,
        ActionDefinitionContext actionContext
    ) {
        return new PolicyAnnotationGenerator(context, actionContext);
    }
}

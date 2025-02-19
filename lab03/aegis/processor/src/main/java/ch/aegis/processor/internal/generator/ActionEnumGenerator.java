package ch.aegis.processor.internal.generator;

import ch.aegis.processor.internal.util.ActionDefinitionContext;
import ch.aegis.processor.internal.util.AnnotationProcessorContext;
import ch.aegis.processor.internal.util.ApiConstants;
import ch.aegis.processor.internal.util.NamingUtil;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

/**
 * Generates an Action enum containing all defined actions in the codebase. The enum implements the
 * RoleEnumerable interface and provides a mapping between action class names and their
 * corresponding enum values.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public final class ActionEnumGenerator {

    private static final String GENERATED_PACKAGE = "ch.aegis.security";
    private static final String GENERATED_ENUM_NAME = "AegisAction";

    private final AnnotationProcessorContext context;

    private ActionEnumGenerator(AnnotationProcessorContext context) {
        this.context = context;
    }

    public void generate() throws IOException {
        var enumBuilder = createEnumBuilder();
        addEnumConstants(enumBuilder);
        addFields(enumBuilder);
        addConstructor(enumBuilder);
        addGetRequiredAttributesMethod(enumBuilder);
        addGetActionClassMethod(enumBuilder);

        var javaFile = JavaFile.builder(GENERATED_PACKAGE, enumBuilder.build())
            .skipJavaLangImports(true)
            .build();

        javaFile.writeTo(context.getFiler());
    }

    private TypeSpec.Builder createEnumBuilder() {
        return TypeSpec.enumBuilder(GENERATED_ENUM_NAME)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(GeneratorUtils.createGeneratedAnnotation())
            .addSuperinterface(
                ParameterizedTypeName.get(
                    ApiConstants.getActionEnumerableInterface(),
                    ClassName.get(GENERATED_PACKAGE, GENERATED_ENUM_NAME)
                )
            );
    }

    private void addEnumConstants(TypeSpec.Builder enumBuilder) {
        context
            .getActionDefinitions()
            .stream()
            .sorted(
                Comparator.comparing(actionContext ->
                    actionContext.actionClass().getSimpleName().toString()
                )
            )
            .forEach(actionContext -> {
                var enumName = NamingUtil.convertCamelCaseToScreamingSnakeCase(
                    actionContext.actionClass().getSimpleName().toString()
                );
                enumBuilder.addEnumConstant(enumName, createEnumConstant(actionContext));
            });
    }

    private TypeSpec createEnumConstant(ActionDefinitionContext actionContext) {
        // Collect all required attributes from all policies
        var requiredAttributes = actionContext
            .policies()
            .stream()
            .flatMap(policy -> policy.requiredAttributes().stream())
            .collect(Collectors.toSet());

        // Convert to Class[] array initializer
        var attributesArray = CodeBlock.builder()
            .add("new Class<?>[] {")
            .add(
                requiredAttributes
                    .stream()
                    .map(attr -> CodeBlock.of("$T.class", attr))
                    .collect(CodeBlock.joining(", "))
            )
            .add("}")
            .add(",")
            .add("$S", actionContext.actionClass().toString())
            .build();

        return TypeSpec.anonymousClassBuilder(attributesArray).build();
    }

    private void addFields(TypeSpec.Builder enumBuilder) {
        enumBuilder.addField(
            ArrayTypeName.of(Class.class),
            "requiredAttributes",
            Modifier.PRIVATE,
            Modifier.FINAL
        );
        enumBuilder.addField(
            String.class,
            "actionClassDescriptor",
            Modifier.PRIVATE,
            Modifier.FINAL
        );
    }

    private void addConstructor(TypeSpec.Builder enumBuilder) {
        var constructor = MethodSpec.constructorBuilder()
            .addParameter(ArrayTypeName.of(Class.class), "requiredAttributes")
            .addParameter(String.class, "actionClassDescriptor")
            .addStatement("this.$1N = $1N", "requiredAttributes")
            .addStatement("this.$1N = $1N", "actionClassDescriptor")
            .build();

        enumBuilder.addMethod(constructor);
    }

    private void addGetRequiredAttributesMethod(TypeSpec.Builder enumBuilder) {
        var getRequiredAttributes = MethodSpec.methodBuilder("getRequiredAttributes")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(ArrayTypeName.of(Class.class))
            .addStatement("return requiredAttributes")
            .build();

        enumBuilder.addMethod(getRequiredAttributes);
    }

    private void addGetActionClassMethod(TypeSpec.Builder enumBuilder) {
        var getActionClass = MethodSpec.methodBuilder("getActionClass")
            .addModifiers(Modifier.PUBLIC)
            .returns(Class.class)
            .addException(ClassNotFoundException.class)
            .addStatement("return Class.forName(actionClassDescriptor)")
            .build();

        enumBuilder.addMethod(getActionClass);
    }

    /**
     * Creates a new instance of {@link ActionEnumGenerator} from the provided context.
     *
     * @param context the annotation processor context
     * @return a new {@link ActionEnumGenerator} instance
     */
    public static ActionEnumGenerator fromContext(AnnotationProcessorContext context) {
        return new ActionEnumGenerator(context);
    }
}

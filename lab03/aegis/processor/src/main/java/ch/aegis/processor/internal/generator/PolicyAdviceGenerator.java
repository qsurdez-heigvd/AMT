package ch.aegis.processor.internal.generator;

import ch.aegis.processor.internal.util.ActionDefinitionContext;
import ch.aegis.processor.internal.util.AnnotationProcessorContext;
import ch.aegis.processor.internal.util.ApiConstants;
import ch.aegis.processor.internal.util.NamingUtil;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.WildcardTypeName;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Generator for Spring AOP advice classes that enforce Aegis security policies. For each action
 * class annotated with {@code @ActionDefinition}, this generator creates a corresponding advice
 * class that:
 *
 * <ul>
 * <li>Intercepts method calls on Spring controllers using AspectJ pointcuts</li>
 * <li>Resolves required attributes using the {@code AttributeResolutionService}</li>
 * <li>Evaluates all defined policies for the action</li>
 * <li>Merges policy results according to the action's effect (ANY/ALL)</li>
 * <li>Enforces access control by throwing {@code UnauthorizedActionException} when needed</li>
 * </ul>
 *
 * <p>The generated advice class supports:
 * <ul>
 * <li>SpEL expressions for attribute overrides via policy annotations</li>
 * <li>Automatic attribute resolution from method parameters</li>
 * <li>Detailed authorization failure messages with policy contexts</li>
 * </ul>
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @see ch.aegis.annotation.ActionDefinition
 * @see ch.aegis.annotation.ActionPolicy
 * @since 1.0
 */
public final class PolicyAdviceGenerator {

    // Spring and AspectJ class names
    private static final ClassName ASPECT = ClassName.get("org.aspectj.lang.annotation", "Aspect");
    private static final ClassName COMPONENT = ClassName.get("org.springframework.stereotype",
        "Component");
    private static final ClassName BEFORE = ClassName.get("org.aspectj.lang.annotation", "Before");
    private static final ClassName POINTCUT = ClassName.get(
        "org.aspectj.lang.annotation",
        "Pointcut"
    );
    private static final ClassName JOIN_POINT = ClassName.get("org.aspectj.lang", "JoinPoint");
    private static final ClassName BEAN_FACTORY = ClassName.get(
        "org.springframework.beans.factory",
        "BeanFactory"
    );
    private static final ClassName CONTROLLER = ClassName.get(
        "org.springframework.stereotype",
        "Controller"
    );
    private static final ClassName REST_CONTROLLER = ClassName.get(
        "org.springframework.web.bind.annotation",
        "RestController"
    );
    private static final ClassName SLF4J = ClassName.get("lombok.extern.slf4j", "Slf4j");
    private static final ClassName STRING_UTILS = ClassName.get(
        "org.springframework.util",
        "StringUtils"
    );
    private static final ClassName ANNOTATION_UTILS = ClassName.get(
        "org.springframework.core.annotation",
        "AnnotationUtils"
    );
    private static final ClassName SPEL_PARSER = ClassName.get(
        "org.springframework.expression.spel.standard",
        "SpelExpressionParser"
    );
    private static final ClassName EVALUATION_CONTEXT = ClassName.get(
        "org.springframework.expression",
        "EvaluationContext"
    );
    private static final ClassName STANDARD_EVALUATION_CONTEXT = ClassName.get(
        "org.springframework.expression.spel.support",
        "StandardEvaluationContext"
    );
    private static final ClassName BEAN_FACTORY_RESOLVER = ClassName.get(
        "org.springframework.context.expression",
        "BeanFactoryResolver"
    );
    private static final ClassName POLICY_VALIDATION_SERVICE = ClassName.get(
        "ch.aegis.contract",
        "PolicyValidationService"
    );

    private final AnnotationProcessorContext context;
    private final ActionDefinitionContext actionContext;

    private PolicyAdviceGenerator(
        AnnotationProcessorContext context,
        ActionDefinitionContext actionContext
    ) {
        this.context = context;
        this.actionContext = actionContext;
    }

    /**
     * Generates the policy advice class file. This class is used to enforce security policies on
     * Spring controllers by intercepting method calls and evaluating the defined policies.
     *
     * @throws IOException if the file cannot be written
     */
    public void generate() throws IOException {
        generatePolicyService();
        generatePolicyAspect();
    }

    private void generatePolicyService() throws IOException {
        var classBuilder = TypeSpec.classBuilder(buildPolicyServiceClassName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(SLF4J)
            .addAnnotation(COMPONENT)
            .addSuperinterface(ParameterizedTypeName.get(
                POLICY_VALIDATION_SERVICE,
                ClassName.get("ch.aegis.security", "AegisAction"),
                ClassName.get(getTargetPackage(), getAuthorizationAnnotationName())
            ))
            .addJavadoc(
                "Service implementation for validating {@link $L} policies.\n\n@generated",
                getAuthorizationAnnotationName()
            );

        addEnforcedActionField(classBuilder);
        addServiceFields(classBuilder);
        addServiceConstructor(classBuilder);
        addValidatePolicyMethod(classBuilder);
        addEvaluatePolicyMethod(classBuilder);
        addAttributeResolutionMethods(classBuilder);

        var javaFile = JavaFile.builder(getTargetPackage(), classBuilder.build())
            .skipJavaLangImports(true)
            .addStaticImport(
                ClassName.get("ch.aegis", "PolicyResult", "ContextualPolicyResult"),
                "withContext"
            )
            .build();

        javaFile.writeTo(context.getFiler());
    }

    private void generatePolicyAspect() throws IOException {
        var classBuilder = TypeSpec.classBuilder(buildPolicyAspectClassName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(SLF4J)
            .addAnnotation(ASPECT)
            .addAnnotation(COMPONENT)
            .addAnnotation(ApiConstants.getPolicyAspectAnnotation())
            .addAnnotation(ClassName.get("lombok", "RequiredArgsConstructor"))
            .addAnnotation(GeneratorUtils.createGeneratedAnnotation())
            .addJavadoc(
                "Aspect handler for {@link $L} annotations.\n\n@generated",
                getAuthorizationAnnotationName()
            );

        addEnforcedActionField(classBuilder);
        addAspectFields(classBuilder);
        addPointcuts(classBuilder);
        addBeforePolicyMethod(classBuilder);
        addUtilityMethods(classBuilder);

        var javaFile = JavaFile.builder(getTargetPackage(), classBuilder.build())
            .skipJavaLangImports(true)
            .build();

        javaFile.writeTo(context.getFiler());
    }

    private void addServiceConstructor(TypeSpec.Builder classBuilder) {
        classBuilder.addMethod(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    ClassName.get(
                        "ch.aegis.contract",
                        "AttributeResolutionService"
                    ),
                    "attributeResolutionService"
                )
                .addStatement("this.attributeResolutionService = attributeResolutionService")
                .addStatement("this.spelParser = new $T()", SPEL_PARSER)
                .addStatement("this.policy = new $T()", ClassName.get(actionContext.actionClass()))
                .build()
        );
    }

    private void addAspectFields(TypeSpec.Builder classBuilder) {
        classBuilder.addField(BEAN_FACTORY, "beanFactory", Modifier.PRIVATE, Modifier.FINAL);
        classBuilder.addField(
            ParameterizedTypeName.get(
                POLICY_VALIDATION_SERVICE,
                ClassName.get("ch.aegis.security", "AegisAction"),
                ClassName.get(getTargetPackage(), getAuthorizationAnnotationName())
            ),
            "policyService",
            Modifier.PRIVATE,
            Modifier.FINAL
        );
    }

    private void addValidatePolicyMethod(TypeSpec.Builder classBuilder) {
        var methodBuilder = MethodSpec.methodBuilder("validatePolicy")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(void.class)
            .addParameter(ClassName.get("ch.aegis.security", "AegisAction"), "action")
            .addParameter(
                ClassName.get(getTargetPackage(), getAuthorizationAnnotationName()),
                "annotation"
            )
            .addParameter(
                ParameterizedTypeName.get(
                    ClassName.get(Set.class),
                    WildcardTypeName.subtypeOf(ClassName.get("ch.aegis.model", "Attribute"))
                ),
                "attributes"
            )
            .addParameter(EVALUATION_CONTEXT, "evaluationContext");

        var attributes = actionContext.policies().stream()
            .flatMap(policy -> policy.requiredAttributes().stream())
            .collect(Collectors.toSet());

        var code = new StringBuilder();
        var varDeclarations = new StringBuilder();
        var parameterList = new StringBuilder();

        code.append("if (action != ENFORCED_ACTION) {\n")
            .append("    throw new IllegalStateException(\"Given action mismatch\");\n")
            .append("}\n\n");

        for (var attr : attributes) {
            var paramName = buildParameterName(attr);
            varDeclarations.append(
                String.format(
                    "var %s = resolve%s(annotation, attributes, evaluationContext);\n",
                    paramName,
                    attr.getSimpleName()
                )
            );
            if (parameterList.length() > 0) {
                parameterList.append(", ");
            }
            parameterList.append(paramName);
        }

        code.append(varDeclarations)
            .append("\nvar result = evaluatePolicy(")
            .append(parameterList)
            .append(");\n")
            .append("if (!result.isGranted()) {\n")
            .append("    throw new $T(\n")
            .append("        ENFORCED_ACTION,\n")
            .append("        result.toString()\n")
            .append("    );\n")
            .append("}\n");

        methodBuilder.addCode(
            CodeBlock.builder()
                .add(code.toString(), ClassName.get("ch.aegis.exception", "UnauthorizedActionException"))
                .build()
        );

        classBuilder.addMethod(methodBuilder.build());
    }

    private String buildPolicyServiceClassName() {
        return actionContext.actionClass().getSimpleName() + "PolicyService";
    }

    private String buildPolicyAspectClassName() {
        return actionContext.actionClass().getSimpleName() + "PolicyAspect";
    }

    private String getAuthorizationAnnotationName() {
        return "Authorize" + actionContext.actionClass().getSimpleName();
    }

    private String buildParameterName(TypeElement attribute) {
        var name = attribute.getSimpleName().toString();
        // Remove "Attribute" suffix if present
        if (name.endsWith("Attribute")) {
            name = name.substring(0, name.length() - "Attribute".length());
        }
        // Convert first character to lowercase
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private String getTargetPackage() {
        return context
            .getElements()
            .getPackageOf(actionContext.actionClass())
            .getQualifiedName()
            .toString();
    }

    private void addEnforcedActionField(TypeSpec.Builder classBuilder) {
        classBuilder.addField(
            FieldSpec.builder(
                    ClassName.get("ch.aegis.security", "AegisAction"),
                    "ENFORCED_ACTION",
                    Modifier.PUBLIC,
                    Modifier.STATIC,
                    Modifier.FINAL
                )
                .initializer(
                    "$T.$L",
                    ClassName.get("ch.aegis.security", "AegisAction"),
                    NamingUtil.convertCamelCaseToScreamingSnakeCase(
                        actionContext.actionClass().getSimpleName().toString()
                    )
                )
                .build()
        );
    }

    private void addPointcuts(TypeSpec.Builder classBuilder) {
        // Default controller pointcut
        classBuilder.addMethod(
            MethodSpec.methodBuilder("defaultControllerBean")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(POINTCUT)
                        .addMember("value", "$S", "within(@" + CONTROLLER.canonicalName() + " *)")
                        .build()
                )
                .build()
        );

        // Rest controller pointcut
        classBuilder.addMethod(
            MethodSpec.methodBuilder("restControllerBean")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(POINTCUT)
                        .addMember(
                            "value",
                            "$S",
                            "within(@" + REST_CONTROLLER.canonicalName() + " *)"
                        )
                        .build()
                )
                .build()
        );

        // Combined controller pointcut
        classBuilder.addMethod(
            MethodSpec.methodBuilder("controllerBean")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(POINTCUT)
                        .addMember("value", "$S", "defaultControllerBean() || restControllerBean()")
                        .build()
                )
                .build()
        );
    }

    private void addBeforePolicyMethod(TypeSpec.Builder classBuilder) {
        var annotationName = getAuthorizationAnnotationName();
        classBuilder.addMethod(
            MethodSpec.methodBuilder("beforePolicy")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(BEFORE)
                        .addMember(
                            "value",
                            "$S",
                            "controllerBean() && @annotation(policyAnnotation)"
                        )
                        .build()
                )
                .addParameter(JOIN_POINT, "joinPoint")
                .addParameter(ClassName.get(getTargetPackage(), annotationName), "policyAnnotation")
                .addCode(
                    """
                    var evaluationContext = generateEvaluationContext(joinPoint.getThis(), joinPoint.getArgs());
                    var attributes = generateAttributes(joinPoint);
                    var policy = $T.synthesizeAnnotation(
                        policyAnnotation,
                        $L.class
                    );
                    
                    policyService.validatePolicy(ENFORCED_ACTION, policy, attributes, evaluationContext);
                    """,
                    ANNOTATION_UTILS,
                    annotationName
                )
                .build()
        );
    }

    private void addServiceFields(TypeSpec.Builder classBuilder) {
        classBuilder.addField(
            ClassName.get("ch.aegis.contract", "AttributeResolutionService"),
            "attributeResolutionService",
            Modifier.PRIVATE,
            Modifier.FINAL
        );
        classBuilder.addField(SPEL_PARSER, "spelParser", Modifier.PRIVATE, Modifier.FINAL);
        classBuilder.addField(
            ClassName.get(actionContext.actionClass()),
            "policy",
            Modifier.PRIVATE,
            Modifier.FINAL
        );
    }

    private void addUtilityMethods(TypeSpec.Builder classBuilder) {
        // Add generateEvaluationContext method
        classBuilder.addMethod(
            MethodSpec.methodBuilder("generateEvaluationContext")
                .addModifiers(Modifier.PRIVATE)
                .returns(EVALUATION_CONTEXT)
                .addParameter(Object.class, "invocationThis")
                .addParameter(Object[].class, "methodArgs")
                .addJavadoc(
                    """
                        Generate an evaluation context for the given method invocation, will provide the application
                        beans for SpEL bean injection and the method arguments as variables.
                        """
                )
                .addCode(generateEvaluationContextMethodBody())
                .build()
        );

        // Add generateAttributes method
        classBuilder.addMethod(
            MethodSpec.methodBuilder("generateAttributes")
                .addModifiers(Modifier.PRIVATE)
                .returns(
                    ParameterizedTypeName.get(
                        ClassName.get(Set.class),
                        WildcardTypeName.subtypeOf(ClassName.get("ch.aegis.model", "Attribute"))
                    )
                )
                .addParameter(JOIN_POINT, "joinPoint")
                .addCode(generateAttributesMethodBody())
                .build()
        );
    }

    private void addEvaluatePolicyMethod(TypeSpec.Builder classBuilder) {
        var attributes = actionContext
            .policies()
            .stream()
            .flatMap(policy -> policy.requiredAttributes().stream())
            .collect(Collectors.toSet());

        var methodBuilder = MethodSpec.methodBuilder("evaluatePolicy")
            .addModifiers(Modifier.PRIVATE)
            .returns(ClassName.get("ch.aegis.PolicyResult", "ContextualPolicyResult"));

        attributes.forEach(attr ->
            methodBuilder.addParameter(ClassName.get(attr), buildParameterName(attr))
        );

        var codeBuilder = CodeBlock.builder();

        for (var policy : actionContext.policies()) {
            var policyName = policy.method().getSimpleName().toString();
            var policyVarName = "policy_" + policyName;

            codeBuilder
                .add("var $L = withContext(\n", policyVarName)
                .indent()
                .add("policy.$L(", policyName);

            var paramJoiner = new StringJoiner(", ");
            policy.requiredAttributes().forEach(attr -> paramJoiner.add(buildParameterName(attr)));
            codeBuilder.add("$L", paramJoiner.toString());

            codeBuilder
                .add("),\n")
                .add(
                    "$S,\n",
                    Optional.ofNullable(policy.policy().name().getValue()).orElse(
                        actionContext.actionClass().getSimpleName() + "." + policyName
                    )
                )
                .add("$S\n", policy.policy().description().get())
                .unindent()
                .add(");\n\n");
        }

        codeBuilder
            .add(
                "return $T.mergeWithContext(\n",
                ClassName.get("ch.aegis.PolicyResult", "ContextualPolicyResult")
            )
            .indent()
            .add("$S,\n", actionContext.actionClass().getSimpleName())
            .add(
                "$T.$L,\n",
                ClassName.get("ch.aegis", "PolicyEffect"),
                actionContext.definition().effect().get()
            )
            .add(
                actionContext
                    .policies()
                    .stream()
                    .map(p -> "policy_" + p.method().getSimpleName().toString())
                    .collect(Collectors.joining(",\n"))
            )
            .add("\n")
            .unindent()
            .add(");");

        methodBuilder.addCode(codeBuilder.build());
        classBuilder.addMethod(methodBuilder.build());
    }

    private void addAttributeResolutionMethods(TypeSpec.Builder classBuilder) {
        var attributes = actionContext
            .policies()
            .stream()
            .flatMap(policy -> policy.requiredAttributes().stream())
            .collect(Collectors.toSet());

        for (var attr : attributes) {
            var methodName = "resolve" + attr.getSimpleName();
            var parameterName = buildParameterName(attr);

            var methodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.get(attr))
                .addParameter(
                    ClassName.get(getTargetPackage(), getAuthorizationAnnotationName()),
                    "policy"
                )
                .addParameter(
                    ParameterizedTypeName.get(
                        ClassName.get(Set.class),
                        WildcardTypeName.subtypeOf(ClassName.get("ch.aegis.model", "Attribute"))
                    ),
                    "params"
                )
                .addParameter(EVALUATION_CONTEXT, "context");

            methodBuilder.addCode(
                """
                    if (!$T.hasText(policy.$L())) {
                        return attributeResolutionService.resolve(params, $T.class);
                    }
                    
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "Overriding default resolution for $L attribute in @$L"
                        );
                    }
                    
                    var object = spelParser.parseExpression(policy.$L()).getValue(context);
                    if (object instanceof $T $L) {
                        return $L;
                    }
                    
                    throw new IllegalStateException(
                        "The expression provided for $L in @$L did not resolve to a $T"
                    );
                    """,
                STRING_UTILS,
                parameterName,
                attr,
                parameterName,
                getAuthorizationAnnotationName(),
                parameterName,
                attr,
                parameterName,
                parameterName,
                parameterName,
                getAuthorizationAnnotationName(),
                attr
            );

            classBuilder.addMethod(methodBuilder.build());
        }
    }

    private CodeBlock generateEvaluationContextMethodBody() {
        return CodeBlock.builder()
            .addStatement("var context = new $T(invocationThis)", STANDARD_EVALUATION_CONTEXT)
            .addStatement("context.setBeanResolver(new $T(beanFactory))", BEAN_FACTORY_RESOLVER)
            .addStatement("context.setVariable(\"args\", methodArgs)")
            .beginControlFlow("for (int i = 0; i < methodArgs.length; ++i)")
            .addStatement("context.setVariable(\"a%d\".formatted(i), methodArgs[i])")
            .addStatement("context.setVariable(\"p%d\".formatted(i), methodArgs[i])")
            .endControlFlow()
            .addStatement("return context")
            .build();
    }

    private CodeBlock generateAttributesMethodBody() {
        return CodeBlock.builder()
            .addStatement(
                "var result = new $T<$T>()",
                HashSet.class,
                ClassName.get("ch.aegis.model", "Attribute")
            )
            .beginControlFlow("for (var arg : joinPoint.getArgs())")
            .beginControlFlow(
                "if (arg instanceof $T attr)",
                ClassName.get("ch.aegis.model", "Attribute")
            )
            .addStatement("result.add(attr)")
            .endControlFlow()
            .endControlFlow()
            .addStatement("return result")
            .build();
    }

    public static PolicyAdviceGenerator of(
        AnnotationProcessorContext context,
        ActionDefinitionContext actionContext
    ) {
        return new PolicyAdviceGenerator(context, actionContext);
    }
}

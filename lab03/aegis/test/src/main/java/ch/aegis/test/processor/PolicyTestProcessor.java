package ch.aegis.test.processor;

import com.google.auto.service.AutoService;
import com.palantir.javapoet.AnnotationSpec;
import com.palantir.javapoet.ArrayTypeName;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import com.palantir.javapoet.WildcardTypeName;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Secondary processor for the Aegis authorization framework, responsible for creating helper test
 * classes for Spring Security and verifying the policies defined in the annotations.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedOptions({})
@SupportedAnnotationTypes("ch.aegis.test.annotation.EnableAegisSpringSecurityTesting")
public final class PolicyTestProcessor extends AbstractProcessor {

    /**
     * Whether this processor claims all processed annotations exclusively or not.
     */
    private static final boolean ANNOTATIONS_CLAIMED_EXCLUSIVELY = false;

    private static final String GENERATED_BY = "PolicyTestProcessor";
    private static final String BASE_PACKAGE = "ch.aegis.test.spring.security";

    // Common ClassNames
    private static final ClassName CLASS_AEGIS_ACTION = ClassName.get("ch.aegis.security", "AegisAction");
    private static final ClassName CLASS_ELEMENT_TYPE = ClassName.get("java.lang.annotation", "ElementType");
    private static final ClassName CLASS_RETENTION_POLICY = ClassName.get("java.lang.annotation", "RetentionPolicy");
    private static final ClassName CLASS_DOCUMENTED = ClassName.get("java.lang.annotation", "Documented");
    private static final ClassName CLASS_INHERITED = ClassName.get("java.lang.annotation", "Inherited");
    private static final ClassName CLASS_GENERATED = ClassName.get("javax.annotation.processing", "Generated");

    // Spring-related ClassNames
    private static final ClassName CLASS_BEAN_FACTORY = ClassName.get("org.springframework.beans.factory", "BeanFactory");
    private static final ClassName CLASS_AUTOWIRED = ClassName.get("org.springframework.beans.factory.annotation", "Autowired");
    private static final ClassName CLASS_STRING_UTILS = ClassName.get("org.springframework.util", "StringUtils");
    private static final ClassName CLASS_SECURITY_CONTEXT = ClassName.get("org.springframework.security.core.context", "SecurityContext");
    private static final ClassName CLASS_SECURITY_CONTEXT_HOLDER = ClassName.get("org.springframework.security.core.context", "SecurityContextHolder");
    private static final ClassName CLASS_USER_DETAILS_SERVICE = ClassName.get("org.springframework.security.core.userdetails", "UserDetailsService");
    private static final ClassName CLASS_USERNAME_PASSWORD_TOKEN = ClassName.get("org.springframework.security.authentication", "UsernamePasswordAuthenticationToken");
    private static final ClassName CLASS_WITH_SECURITY_CONTEXT = ClassName.get("org.springframework.security.test.context.support", "WithSecurityContext");
    private static final ClassName CLASS_WITH_SECURITY_CONTEXT_FACTORY = ClassName.get("org.springframework.security.test.context.support", "WithSecurityContextFactory");
    private static final ClassName CLASS_COMPONENT_SCAN = ClassName.get("org.springframework.context.annotation", "ComponentScan");
    private static final ClassName CLASS_COMPONENT_SCAN_FILTER = CLASS_COMPONENT_SCAN.nestedClass("Filter");
    private static final ClassName CLASS_FILTER_TYPE = ClassName.get("org.springframework.context.annotation", "FilterType");
    private static final ClassName CLASS_CONFIGURATION = ClassName.get("org.springframework.context.annotation", "Configuration");
    private static final ClassName CLASS_ENABLE_ASPECT_J_PROXY = ClassName.get("org.springframework.context.annotation", "EnableAspectJAutoProxy");
    private static final ClassName CLASS_IMPORT = ClassName.get("org.springframework.context.annotation", "Import");
    private static final ClassName CLASS_WEB_MVC_TEST = ClassName.get("org.springframework.boot.test.autoconfigure.web.servlet", "WebMvcTest");
    private static final ClassName CLASS_ALIAS_FOR = ClassName.get("org.springframework.core.annotation", "AliasFor");

    // Aegis-related ClassNames
    private static final ClassName CLASS_POLICY_VALIDATION_SERVICE = ClassName.get("ch.aegis.contract", "PolicyValidationService");
    private static final ClassName CLASS_ATTRIBUTE = ClassName.get("ch.aegis.model", "Attribute");
    private static final ClassName CLASS_POLICY_ASPECT = ClassName.get("ch.aegis.annotation", "PolicyAspect");
    private static final ClassName CLASS_UNAUTHORIZED_ACTION_EXCEPTION = ClassName.get("ch.aegis.exception", "UnauthorizedActionException");
    private static final ClassName CLASS_TESTING_DETAILS = ClassName.get(BASE_PACKAGE, "TestingDetails");
    private static final ClassName CLASS_WITH_USER_ACTION = ClassName.get(BASE_PACKAGE, "WithUserAction");

    // Other utility ClassNames
    private static final ClassName CLASS_EVALUATION_CONTEXT = ClassName.get("org.springframework.expression", "EvaluationContext");
    private static final ClassName CLASS_ILLEGAL_STATE_EXCEPTION = ClassName.get("java.lang", "IllegalStateException");
    private static final ClassName CLASS_ANNOTATION = ClassName.get("java.lang.annotation", "Annotation");
    private static final ClassName CLASS_CLASS = ClassName.get(Class.class);

    private boolean generated = false;

    public PolicyTestProcessor() {
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() || annotations.isEmpty() || generated) {
            return ANNOTATIONS_CLAIMED_EXCLUSIVELY;
        }

        try {
            var packageName = getAnnotatedPackageName(roundEnv);
            generateTestingDetails();
            generateWithUserAction();
            generateWithUserActionSecurityContextFactory();
            generateAegisTestConfiguration(packageName);
            generateAegisWebMvcTest(packageName);

            generated = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate test utilities", e);
        }

        return ANNOTATIONS_CLAIMED_EXCLUSIVELY;
    }

    private String getAnnotatedPackageName(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils().getTypeElement(
                    "ch.aegis.test.annotation.EnableAegisSpringSecurityTesting"
                )
            )
            .stream()
            .map(element -> processingEnv.getElementUtils().getPackageOf(element).getQualifiedName()
                .toString())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "No package found with @EnableAegisSpringSecurityTesting"));
    }

    private void generateTestingDetails() throws IOException {
        var generated = createGeneratedAnnotation();

        var testingDetails = TypeSpec.recordBuilder("TestingDetails")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(generated)
            .addJavadoc("A testing user principal for use in tests.\n\n@generated by $L\n", GENERATED_BY)
            .recordConstructor(
                MethodSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder(CLASS_AEGIS_ACTION, "testedAction").build())
                    .addParameter(ParameterSpec.builder(boolean.class, "authorized").build())
                    .build()
            )
            .build();

        var javaFile = JavaFile.builder(BASE_PACKAGE, testingDetails)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private void generateWithUserAction() throws IOException {
        var generated = createGeneratedAnnotation();

        var withUserAction = TypeSpec.annotationBuilder("WithUserAction")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(generated)
            .addAnnotation(AnnotationSpec.builder(CLASS_DOCUMENTED).build())
            .addAnnotation(AnnotationSpec.builder(ClassName.get("java.lang.annotation", "Target"))
                .addMember("value", "{$T.METHOD, $T.TYPE}", CLASS_ELEMENT_TYPE, CLASS_ELEMENT_TYPE)
                .build())
            .addAnnotation(AnnotationSpec.builder(ClassName.get("java.lang.annotation", "Retention"))
                .addMember("value", "$T.RUNTIME", CLASS_RETENTION_POLICY)
                .build())
            .addAnnotation(AnnotationSpec.builder(CLASS_WITH_SECURITY_CONTEXT)
                .addMember("factory", "$T.class", ClassName.get(BASE_PACKAGE, "WithUserActionSecurityContextFactory"))
                .build())
            .addJavadoc("Annotation for test methods that need to run with a mocked user context with specific\n" +
                "authorization settings.\n\n" +
                "@generated by $L\n", GENERATED_BY)
            .addMethod(MethodSpec.methodBuilder("authorized")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(boolean.class)
                .defaultValue("true")
                .addJavadoc("Specifies whether the user should be authorized for the given action.\n\n" +
                    "@return true if the user should be authorized, false otherwise\n")
                .build())
            .addMethod(MethodSpec.methodBuilder("action")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(CLASS_AEGIS_ACTION)
                .addJavadoc("Alias for value().\n\n@return the action enum value\n")
                .build())
            .addMethod(MethodSpec.methodBuilder("username")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(String.class)
                .defaultValue("$S", "user")
                .addJavadoc("The username to look up in the {@link UserDetailsService}\n\n" +
                    "@return the username to look up in the {@link UserDetailsService}\n")
                .build())
            .addMethod(MethodSpec.methodBuilder("userDetailsServiceBeanName")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(String.class)
                .defaultValue("$S", "")
                .addJavadoc("The bean name for the {@link UserDetailsService} to use. If this is not provided, then the\n" +
                    "lookup is done by type and expects only a single {@link UserDetailsService} bean to be\n" +
                    "exposed.\n\n" +
                    "@return the bean name for the {@link UserDetailsService} to use.\n")
                .build())
            .build();

        var javaFile = JavaFile.builder(BASE_PACKAGE, withUserAction)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private void generateWithUserActionSecurityContextFactory() throws IOException {
        var generated = createGeneratedAnnotation();

        var factory = TypeSpec.classBuilder("WithUserActionSecurityContextFactory")
            .addModifiers(Modifier.FINAL)
            .addAnnotation(generated)
            .addJavadoc("Factory for creating test security contexts.\n\n@generated by $L\n", GENERATED_BY)
            .addSuperinterface(ParameterizedTypeName.get(
                CLASS_WITH_SECURITY_CONTEXT_FACTORY,
                CLASS_WITH_USER_ACTION))
            .addField(CLASS_BEAN_FACTORY, "beans", Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(CLASS_AUTOWIRED)
                .addParameter(CLASS_BEAN_FACTORY, "beans")
                .addStatement("this.beans = beans")
                .build())
            .addMethod(createSecurityContextMethod())
            .addMethod(createFindUserDetailsServiceMethod())
            .build();

        var javaFile = JavaFile.builder(BASE_PACKAGE, factory)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private MethodSpec createSecurityContextMethod() {
        return MethodSpec.methodBuilder("createSecurityContext")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(CLASS_SECURITY_CONTEXT)
            .addParameter(CLASS_WITH_USER_ACTION, "annotation")
            .addCode(CodeBlock.builder()
                .addStatement("var beanName = annotation.userDetailsServiceBeanName()")
                .addStatement("var userDetailsService = findUserDetailsService(beanName)")
                .addStatement("var username = annotation.username()")
                .addStatement("var principal = userDetailsService.loadUserByUsername(username)")
                .addStatement("var authentication = $T.authenticated(\n" +
                    "    principal,\n" +
                    "    principal.getPassword(),\n" +
                    "    principal.getAuthorities()\n" +
                    ")", CLASS_USERNAME_PASSWORD_TOKEN)
                .addStatement("authentication.setDetails(new $T(annotation.action(), annotation.authorized()))",
                    CLASS_TESTING_DETAILS)
                .addStatement("var context = $T.createEmptyContext()",
                    CLASS_SECURITY_CONTEXT_HOLDER)
                .addStatement("context.setAuthentication(authentication)")
                .addStatement("return context")
                .build())
            .build();
    }

    private MethodSpec createFindUserDetailsServiceMethod() {
        return MethodSpec.methodBuilder("findUserDetailsService")
            .addModifiers(Modifier.PRIVATE)
            .returns(CLASS_USER_DETAILS_SERVICE)
            .addParameter(String.class, "beanName")
            .addStatement("return $T.hasLength(beanName)\n" +
                    "    ? this.beans.getBean(beanName, $T.class)\n" +
                    "    : this.beans.getBean($T.class)",
                CLASS_STRING_UTILS,
                CLASS_USER_DETAILS_SERVICE,
                CLASS_USER_DETAILS_SERVICE)
            .build();
    }

    private void generateAegisTestConfiguration(String packageName) throws IOException {
        var generated = createGeneratedAnnotation();

        var configuration = TypeSpec.classBuilder("AegisTestConfiguration")
            .addAnnotation(generated)
            .addAnnotation(CLASS_CONFIGURATION)
            .addAnnotation(CLASS_ENABLE_ASPECT_J_PROXY)
            .addAnnotation(createComponentScanAnnotation(packageName))
            .addJavadoc("Configuration class for Aegis test context.\n")
            .addMethod(createPolicyValidationServiceMethod())
            .build();

        var javaFile = JavaFile.builder(packageName, configuration)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private AnnotationSpec createComponentScanAnnotation(String packageName) {
        return AnnotationSpec.builder(CLASS_COMPONENT_SCAN)
            .addMember("basePackages", "{$S, $S}", packageName, "ch.aegis.test.spring.security")
            .addMember("includeFilters", "@$T(\n" +
                    "    type = $T.ANNOTATION,\n" +
                    "    value = $T.class\n" +
                    ")",
                CLASS_COMPONENT_SCAN_FILTER,
                CLASS_FILTER_TYPE,
                CLASS_POLICY_ASPECT)
            .addMember("useDefaultFilters", "false")
            .build();
    }

    private MethodSpec createPolicyValidationServiceMethod() {
        TypeName policyValidationServiceType = ParameterizedTypeName.get(
            CLASS_POLICY_VALIDATION_SERVICE,
            CLASS_AEGIS_ACTION,
            WildcardTypeName.subtypeOf(Object.class)
        );

        return MethodSpec.methodBuilder("policyValidationService")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(ClassName.get("org.springframework.context.annotation", "Bean"))
            .addAnnotation(ClassName.get("org.springframework.context.annotation", "Primary"))
            .returns(policyValidationServiceType)
            .addCode(CodeBlock.builder()
                .beginControlFlow("return new $T<>()", CLASS_POLICY_VALIDATION_SERVICE)
                .beginControlFlow("@Override\npublic void validatePolicy(\n" +
                        "    $T action,\n" +
                        "    $T annotation,\n" +
                        "    $T<? extends $T> attributes,\n" +
                        "    $T evaluationContext\n" +
                        ")",
                    CLASS_AEGIS_ACTION,
                    CLASS_ANNOTATION,
                    ClassName.get("java.util", "Set"),
                    CLASS_ATTRIBUTE,
                    CLASS_EVALUATION_CONTEXT)
                .addStatement("var authentication = $T.getContext().getAuthentication()",
                    CLASS_SECURITY_CONTEXT_HOLDER)
                .beginControlFlow("if (authentication == null\n" +
                        "    || !(authentication.getDetails() instanceof $T details))",
                    CLASS_TESTING_DETAILS)
                .addStatement("throw new $T($S)",
                    CLASS_ILLEGAL_STATE_EXCEPTION,
                    "No testing details found in security context")
                .endControlFlow()
                .addStatement("$T expectedAction = details.testedAction()",
                    CLASS_AEGIS_ACTION)
                .beginControlFlow("if (!expectedAction.equals(action))")
                .addStatement("throw new $T($S.formatted(expectedAction, action))",
                    CLASS_ILLEGAL_STATE_EXCEPTION,
                    "Expected action %s but got %s")
                .endControlFlow()
                .beginControlFlow("if (!details.authorized())")
                .addStatement("throw new $T(\n" +
                        "    action,\n" +
                        "    $S\n" +
                        ")",
                    CLASS_UNAUTHORIZED_ACTION_EXCEPTION,
                    "Unauthorized by test configuration")
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .add(";")
                .build())
            .build();
    }

    private void generateAegisWebMvcTest(String packageName) throws IOException {
        var generated = createGeneratedAnnotation();

        var webMvcTest = TypeSpec.annotationBuilder("AegisWebMvcTest")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(generated)
            .addAnnotation(AnnotationSpec.builder(ClassName.get("java.lang.annotation", "Target"))
                .addMember("value", "$T.TYPE", CLASS_ELEMENT_TYPE)
                .build())
            .addAnnotation(AnnotationSpec.builder(ClassName.get("java.lang.annotation", "Retention"))
                .addMember("value", "$T.RUNTIME", CLASS_RETENTION_POLICY)
                .build())
            .addAnnotation(CLASS_DOCUMENTED)
            .addAnnotation(CLASS_INHERITED)
            .addAnnotation(CLASS_WEB_MVC_TEST)
            .addAnnotation(AnnotationSpec.builder(CLASS_IMPORT)
                .addMember("value", "$L.class", "AegisTestConfiguration")
                .build())
            .addJavadoc("Meta-annotation that combines {@link WebMvcTest} with the necessary configuration\n" +
                "to properly test Aegis-secured endpoints.\n")
            .addMethod(createWebMvcTestValueMethod())
            .addMethod(createWebMvcTestControllersMethod())
            .addMethod(createWebMvcTestExcludeAutoConfigurationMethod())
            .addMethod(createWebMvcTestExcludeFiltersMethod())
            .addMethod(createWebMvcTestIncludeFiltersMethod())
            .addMethod(createWebMvcTestUseDefaultFiltersMethod())
            .build();

        var javaFile = JavaFile.builder(packageName, webMvcTest)
            .build();

        javaFile.writeTo(processingEnv.getFiler());
    }

    private MethodSpec createWebMvcTestValueMethod() {
        return MethodSpec.methodBuilder("value")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ArrayTypeName.of(CLASS_CLASS))
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "value")
                .build())
            .defaultValue("{ }")
            .addJavadoc("Alias for {@link WebMvcTest#value}.\n")
            .build();
    }

    private MethodSpec createWebMvcTestControllersMethod() {
        return MethodSpec.methodBuilder("controllers")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ArrayTypeName.of(CLASS_CLASS))
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "controllers")
                .build())
            .defaultValue("{ }")
            .addJavadoc("Alias for {@link WebMvcTest#controllers}.\n")
            .build();
    }

    private MethodSpec createWebMvcTestExcludeAutoConfigurationMethod() {
        return MethodSpec.methodBuilder("excludeAutoConfiguration")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ArrayTypeName.of(CLASS_CLASS))
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "excludeAutoConfiguration")
                .build())
            .defaultValue("{ }")
            .addJavadoc("Alias for {@link WebMvcTest#excludeAutoConfiguration}.\n")
            .build();
    }

    private MethodSpec createWebMvcTestExcludeFiltersMethod() {
        return MethodSpec.methodBuilder("excludeFilters")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ArrayTypeName.of(CLASS_COMPONENT_SCAN_FILTER))
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "excludeFilters")
                .build())
            .defaultValue("{ }")
            .addJavadoc("Alias for {@link WebMvcTest#excludeFilters}.\n")
            .build();
    }

    private MethodSpec createWebMvcTestIncludeFiltersMethod() {
        return MethodSpec.methodBuilder("includeFilters")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(ArrayTypeName.of(CLASS_COMPONENT_SCAN_FILTER))
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "includeFilters")
                .build())
            .defaultValue("{\n    @$T(\n        type = $T.ANNOTATION,\n        value = $T.class\n    )\n}",
                CLASS_COMPONENT_SCAN_FILTER,
                CLASS_FILTER_TYPE,
                CLASS_POLICY_ASPECT)
            .addJavadoc("Alias for {@link WebMvcTest#includeFilters}.\n")
            .build();
    }

    private MethodSpec createWebMvcTestUseDefaultFiltersMethod() {
        return MethodSpec.methodBuilder("useDefaultFilters")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(boolean.class)
            .addAnnotation(AnnotationSpec.builder(CLASS_ALIAS_FOR)
                .addMember("annotation", "$T.class", CLASS_WEB_MVC_TEST)
                .addMember("attribute", "$S", "useDefaultFilters")
                .build())
            .defaultValue("true")
            .addJavadoc("Alias for {@link WebMvcTest#useDefaultFilters}.\n")
            .build();
    }

    private AnnotationSpec createGeneratedAnnotation() {
        return AnnotationSpec.builder(CLASS_GENERATED)
            .addMember("value", "$S", "ch.aegis.test.processor.PolicyTestProcessor")
            .addMember("date", "$S", ZonedDateTime.now().toString())
            .build();
    }
}

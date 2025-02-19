package ch.aegis.processor;

import ch.aegis.processor.internal.gem.ActionDefinitionGem;
import ch.aegis.processor.internal.gem.ActionPolicyGem;
import ch.aegis.processor.internal.generator.ActionEnumGenerator;
import ch.aegis.processor.internal.generator.DefaultAttributeResolutionServiceGenerator;
import ch.aegis.processor.internal.generator.DefaultRoleAttributeProviderGenerator;
import ch.aegis.processor.internal.generator.PolicyAdviceGenerator;
import ch.aegis.processor.internal.generator.PolicyAnnotationGenerator;
import ch.aegis.processor.internal.generator.UnauthorizedActionExceptionGenerator;
import ch.aegis.processor.internal.util.ActionDefinitionContext;
import ch.aegis.processor.internal.util.ActionDefinitionContext.PolicyDefinition;
import ch.aegis.processor.internal.util.AnnotationProcessorContext;
import ch.aegis.processor.internal.util.ApiConstants;
import com.google.auto.service.AutoService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.ElementKindVisitor14;

/**
 * Main annotation processor for the Aegis authorization framework, responsible for processing
 * annotations and generating the necessary code to enforce policies. Aegis generates an enumeration
 * of all the declared actions along with Spring interceptors to enforce each policy. A set of
 * annotations is also generated for linking controller endpoints to their interceptors.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedOptions({})
@SupportedAnnotationTypes("ch.aegis.annotation.ActionDefinition")
public final class PolicyProcessor extends AbstractProcessor {

    /**
     * Whether this processor claims all processed annotations exclusively or not.
     */
    private static final boolean ANNOTATIONS_CLAIMED_EXCLUSIVELY = false;

    private AnnotationProcessorContext context;

    public PolicyProcessor() {
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        context = new AnnotationProcessorContext(
            processingEnv.getElementUtils(),
            processingEnv.getTypeUtils(),
            processingEnv.getMessager(),
            processingEnv.getFiler()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            if (context.getRoleEnumerationDefinition() == null) {
                var roleEnum = fetchRoleEnum(roundEnv);
                if (roleEnum == null) {
                    return ANNOTATIONS_CLAIMED_EXCLUSIVELY;
                }

                context.setRoleEnumerationDefinition(roleEnum);
            }

            // We should have been able to process all required annotations by now
            if (annotations.isEmpty() && !context.isGeneratedContextClasses()) {
                try {
                    ActionEnumGenerator.fromContext(context).generate();
                    UnauthorizedActionExceptionGenerator.fromContext(context).generate();
                    DefaultAttributeResolutionServiceGenerator.fromContext(context).generate();
                    DefaultRoleAttributeProviderGenerator.fromContext(context).generate();

                    context.setGeneratedContextClasses(true);
                } catch (Throwable t) {
                    handleUncaughtError(null, t);
                }

                return ANNOTATIONS_CLAIMED_EXCLUSIVELY;
            }

            var actionDefinitions = fetchActionDefinitions(annotations, roundEnv);
            context.getActionDefinitions().addAll(actionDefinitions);
            processDefinitions(actionDefinitions);
        }

        return ANNOTATIONS_CLAIMED_EXCLUSIVELY;
    }

    private TypeElement fetchRoleEnum(RoundEnvironment roundEnv) {
        var roleDef = context.getElements().getTypeElement(ApiConstants.ROLE_DEFINITION_ANNOTATION);
        try {
            var enumerations = roundEnv.getElementsAnnotatedWith(roleDef);
            if (enumerations.size() != 1) {
                context.getMessager().printError(
                    "Exactly one enum must be annotated with @RoleDefinition, found %d".formatted(
                        enumerations.size()
                    ),
                    roleDef
                );
                return null;
            }

            var roleEnum = asTypeElement(enumerations.iterator().next());
            if (roleEnum.getKind() != ElementKind.ENUM) {
                context.getMessager().printError(
                    "@RoleDefinition should be applied to an enum",
                    roleEnum
                );
                return null;
            } else if (!containsRoleEnumerableInterface(roleEnum)) {
                context.getMessager().printError(
                    "Role enum must implement RoleEnumerable",
                    roleEnum
                );
                return null;
            }

            return roleEnum;
        } catch (Throwable t) {
            handleUncaughtError(roleDef, t);
            return null;
        }
    }

    private Set<ActionDefinitionContext> fetchActionDefinitions(
        Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv
    ) {
        Set<ActionDefinitionContext> definitions = new HashSet<>();
        for (var annotation : annotations) {
            if (annotation.getKind() != ElementKind.ANNOTATION_TYPE) {
                // The annotation is not on the classpath, let the compiler deal with that.
                continue;
            }

            try {
                Set<? extends Element> annotatedDefinitions = roundEnv
                    .getElementsAnnotatedWith(annotation);
                for (var element : annotatedDefinitions) {
                    var definitionTypeElement = asTypeElement(element);
                    if (definitionTypeElement != null
                        && ActionDefinitionGem.instanceOn(definitionTypeElement) != null) {
                        definitions.add(resolveActionDefinition(definitionTypeElement));
                    }
                }
            } catch (Throwable t) {
                handleUncaughtError(annotation, t);
                continue;
            }
        }

        return definitions;
    }

    private ActionDefinitionContext resolveActionDefinition(TypeElement definitionClass) {
        var gem = ActionDefinitionGem.instanceOn(definitionClass);
        return new ActionDefinitionContext(
            definitionClass, gem,
            resolveActionPolicies(definitionClass)
        );
    }

    private List<PolicyDefinition> resolveActionPolicies(
        TypeElement definitionClass
    ) {
        return ElementFilter.methodsIn(definitionClass.getEnclosedElements()).stream()
            .filter(method -> ActionPolicyGem.instanceOn(method) != null)
            .map(method -> {
                var policyGem = ActionPolicyGem.instanceOn(method);
                var requiredAttributes = method
                    .getParameters()
                    .stream()
                    .map(VariableElement::asType)
                    .map(type -> context.getTypes().asElement(type))
                    .map(this::asTypeElement)
                    .filter(Objects::nonNull)
                    .toList();
                return new ActionDefinitionContext.PolicyDefinition(
                    method,
                    policyGem,
                    requiredAttributes
                );
            })
            .toList();
    }

    private void processDefinitions(Set<ActionDefinitionContext> definitions) {
        for (var definition : definitions) {
            try {
                PolicyAnnotationGenerator.of(context, definition).generate();
                PolicyAdviceGenerator.of(context, definition).generate();
            } catch (Throwable t) {
                handleUncaughtError(definition.actionClass(), t);
            }
        }
    }

    private void handleUncaughtError(Element element, Throwable thrown) {
        var sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        String reportableStacktrace = sw.toString().replace(System.lineSeparator(), "  ");
        processingEnv.getMessager().printError(
            "Internal error in the mapping processor: " + reportableStacktrace,
            element
        );
    }

    private TypeElement asTypeElement(Element element) {
        return element.accept(new ElementKindVisitor14<TypeElement, Void>() {
            @Override
            public TypeElement visitTypeAsInterface(TypeElement e, Void p) {
                return e;
            }

            @Override
            public TypeElement visitTypeAsClass(TypeElement e, Void p) {
                return e;
            }

            @Override
            public TypeElement visitTypeAsEnum(TypeElement e, Void unused) {
                return e;
            }

            @Override
            public TypeElement visitTypeAsRecord(TypeElement e, Void unused) {
                return e;
            }
        }, null);
    }

    private boolean containsRoleEnumerableInterface(TypeElement roleEnum) {
        return roleEnum.getInterfaces().stream().anyMatch(i -> {
            var element = processingEnv.getTypeUtils().asElement(i);
            return element != null && asTypeElement(element).getQualifiedName().contentEquals(
                "ch.aegis.contract.RoleEnumerable"
            );
        });
    }
}

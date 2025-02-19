package ch.aegis.contract;

import ch.aegis.model.Attribute;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.expression.EvaluationContext;

/**
 * Internal service that validates a given policy annotation against a set of attributes and an
 * expression evaluation context. This service is used by the Aegis framework to validate policies
 * at runtime, and in testing it can be mocked to validate policies in a controlled environment.
 *
 * @param <E> the action enumerable to validate the policy against
 * @param <A> the type of policy annotation to validate
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface PolicyValidationService<E extends Enum<E> & ActionEnumerable<E>, A extends Annotation> {

    /**
     * Validates the given policy annotation against the given set of attributes and evaluation
     * context. If the policy is invalid, an exception should be thrown.
     *
     * @param action            the action to validate the policy against
     * @param annotation        the policy annotation to validate
     * @param attributes        the attributes to validate the policy against
     * @param evaluationContext the evaluation context to use for the validation
     */
    void validatePolicy(
        E action,
        A annotation,
        Set<? extends Attribute> attributes,
        EvaluationContext evaluationContext
    );
}

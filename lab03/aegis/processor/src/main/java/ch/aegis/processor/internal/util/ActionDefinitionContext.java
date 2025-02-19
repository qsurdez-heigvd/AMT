package ch.aegis.processor.internal.util;

import ch.aegis.processor.internal.gem.ActionDefinitionGem;
import ch.aegis.processor.internal.gem.ActionPolicyGem;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Context holding the definition of an action, including the action class, its definition and the
 * policies that are applied to it.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public record ActionDefinitionContext(
    TypeElement actionClass,
    ActionDefinitionGem definition,
    List<PolicyDefinition> policies
) {

    public record PolicyDefinition(
        ExecutableElement method,
        ActionPolicyGem policy,
        List<TypeElement> requiredAttributes
    ) {
    }
}

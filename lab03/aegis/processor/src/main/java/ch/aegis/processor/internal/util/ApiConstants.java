package ch.aegis.processor.internal.util;

import com.palantir.javapoet.ClassName;

/**
 * API annotation and interface name constants. These are used to generate code that references
 * these annotations and interfaces, since the processor does not have direct access to the API
 * module.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public interface ApiConstants {

    String ACTION_DEFINITION_ANNOTATION = "ch.aegis.annotation.ActionDefinition";
    String ACTION_POLICY_ANNOTATION = "ch.aegis.annotation.ActionPolicy";
    String ROLE_DEFINITION_ANNOTATION = "ch.aegis.annotation.RoleDefinition";

    static ClassName getPolicyAspectAnnotation() {
        return ClassName.get("ch.aegis.annotation", "PolicyAspect");
    }

    static ClassName getRoleEnumerableInterface() {
        return ClassName.get("ch.aegis.contract", "RoleEnumerable");
    }

    static ClassName getActionEnumerableInterface() {
        return ClassName.get("ch.aegis.contract", "ActionEnumerable");
    }
}

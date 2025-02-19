package ch.aegis.processor.internal.util;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Main record holding the context of the annotation processor, providing access to the utility
 * classes provided by the Java compiler. Additional data may be added to this record as needed.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class AnnotationProcessorContext {

    private final Elements elements;
    private final Types types;
    private final Messager messager;
    private final Filer filer;

    private TypeElement roleEnumerationDefinition;
    private Set<ActionDefinitionContext> actionDefinitions = new HashSet<>();

    private boolean generatedContextClasses = false;
}

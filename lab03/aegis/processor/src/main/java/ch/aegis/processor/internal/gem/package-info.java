/**
 * This package contains the gem definitions for the Aegis processor annotations. Gems are generated
 * classes that provide a type-safe API for partial reflection access to the annotation values
 * without requiring them to be present on the classpath at compile-time.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@GemDefinition(ActionDefinition.class)
@GemDefinition(ActionPolicy.class)
@GemDefinition(RoleDefinition.class)
package ch.aegis.processor.internal.gem;

import ch.aegis.annotation.ActionDefinition;
import ch.aegis.annotation.ActionPolicy;
import ch.aegis.annotation.RoleDefinition;
import org.mapstruct.tools.gem.GemDefinition;

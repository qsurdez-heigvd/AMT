package ch.heig.amt.vineward.security.attribute;

import ch.aegis.model.Attribute;
import java.util.UUID;

/**
 * Attribute representing a user's ID
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record UserIdAttribute(
    UUID value
) implements Attribute {

}

package ch.heig.amt.vineward.security.attribute;

import ch.aegis.model.Attribute;
import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.Region;

/**
 * Attribute representing a given wine.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record WineAttribute(
    String name,
    Canton canton,
    Region region
) implements Attribute {

}

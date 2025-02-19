package ch.heig.amt.vineward.security.attribute;

import ch.aegis.model.Attribute;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;

/**
 * Attribute representing a given wine.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record ReviewAttribute(
    ReviewStatus status,
    UserIdAttribute author,
    WineAttribute wine
) implements Attribute {

}

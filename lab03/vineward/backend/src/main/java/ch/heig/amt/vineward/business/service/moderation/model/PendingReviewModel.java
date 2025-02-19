package ch.heig.amt.vineward.business.service.moderation.model;

import ch.heig.amt.vineward.api.reference.UserRef;
import ch.heig.amt.vineward.api.reference.WineRef;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;

/**
 * Model representing a pending review in the moderation queue.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record PendingReviewModel(
    Long id,
    String title,
    String body,
    ReviewStatus status,
    WineRef wine,
    UserRef author
) {
}

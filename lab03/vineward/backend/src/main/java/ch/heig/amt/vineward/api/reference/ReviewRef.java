package ch.heig.amt.vineward.api.reference;

import ch.heig.amt.vineward.business.model.review.ReviewStatus;

public record ReviewRef(
    Long id,
    String title,
    ReviewStatus status,
    UserRef author
) {
}

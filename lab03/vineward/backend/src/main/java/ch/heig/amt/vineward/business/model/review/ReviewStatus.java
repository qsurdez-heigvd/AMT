package ch.heig.amt.vineward.business.model.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of review statuses.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    DRAFT("Draft"),
    PENDING_REVIEW("Pending review"),
    VERIFIED("Verified");

    private final String displayName;
}

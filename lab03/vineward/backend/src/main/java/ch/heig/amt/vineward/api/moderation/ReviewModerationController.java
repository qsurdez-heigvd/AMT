package ch.heig.amt.vineward.api.moderation;

import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.service.moderation.ReviewModerationService;
import ch.heig.amt.vineward.business.service.moderation.model.PendingReviewModel;
import ch.heig.amt.vineward.security.attribute.ReviewIdAttribute;
import ch.heig.amt.vineward.security.policy.AuthorizeInspectReview;
import ch.heig.amt.vineward.security.policy.AuthorizeSubmitReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/moderation/reviews")
@Tag(name = "Moderation", description = "Request moderation")
@RequiredArgsConstructor
public class ReviewModerationController {

    private final ReviewRepository reviewRepository;
    private final ReviewModerationService reviewModerationService;

    @Operation(summary = "Submit a review for moderation")
    @PostMapping("/{reviewId}/submit")
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeSubmitReview
    public void submitReview(
        @PathVariable ReviewIdAttribute reviewId
    ) {
        reviewRepository
            .findById(reviewId.getValue())
            .ifPresent(reviewModerationService::submitForModeration);
    }

    @GetMapping("/count")
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    public long getPendingReviewModerationCount() {
        return reviewRepository.countByStatus(ReviewStatus.PENDING_REVIEW);
    }

    @Operation(summary = "Peek at the first review in the queue without removing it")
    @GetMapping("/peek")
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeInspectReview
    public ResponseEntity<PendingReviewModel> peekNextReview() {
        return reviewModerationService.peekNextReview()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Approve the review")
    @PostMapping("/{reviewId}/approve")
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeInspectReview
    public void approveReview(@PathVariable ReviewIdAttribute reviewId) {
        reviewRepository
            .findById(reviewId.getValue())
            .ifPresent(reviewModerationService::approveReview);
    }

    @Operation(summary = "Deny the review")
    @PostMapping("/{reviewId}/deny")
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeInspectReview
    public void denyReview(@PathVariable ReviewIdAttribute reviewId) {
        reviewRepository
            .findById(reviewId.getValue())
            .ifPresent(reviewModerationService::declineReview);

        reviewRepository.deleteById(reviewId.getValue());
    }

}

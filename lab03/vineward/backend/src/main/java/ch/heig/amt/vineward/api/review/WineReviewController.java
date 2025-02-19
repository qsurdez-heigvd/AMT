package ch.heig.amt.vineward.api.review;

import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.api.review.mapping.WineReviewMapper;
import ch.heig.amt.vineward.api.review.request.ReviewRequestBody;
import ch.heig.amt.vineward.api.review.viewmodel.ReviewViewModel;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.business.repository.WineRepository;
import ch.heig.amt.vineward.security.UserSecurityGetter;
import ch.heig.amt.vineward.security.attribute.WineIdAttribute;
import ch.heig.amt.vineward.security.policy.AuthorizeCreateWineReview;
import ch.heig.amt.vineward.security.policy.AuthorizeReadWineReview;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for wine reviews.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@RestController
@RequestMapping(path = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WineReviewController {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final WineReviewMapper reviewMapper;
    private final WineRepository wineRepository;

    @GetMapping("/reviews")
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadWineReview
    public List<ReviewViewModel> getReviews() {
        return reviewRepository
            .findAllByStatus(ReviewStatus.VERIFIED)
            .stream()
            .map(reviewMapper::toViewModel)
            .toList();
    }

    @GetMapping("/reviews/{reviewId}")
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadWineReview
    public ReviewViewModel getReview(@PathVariable Long reviewId) {
        return reviewRepository
            .findById(reviewId)
            .map(reviewMapper::toViewModel)
            .orElseThrow(() -> ObjectNotFoundException.forReview().fromId(reviewId));
    }

    @GetMapping("/wines/{wineId}/reviews")
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadWineReview
    public List<ReviewViewModel> getReviews(@PathVariable WineIdAttribute wineId) {
        return reviewRepository
            .findByWineIdAndStatus(wineId.getValue(), ReviewStatus.VERIFIED)
            .stream()
            .map(reviewMapper::toViewModel)
            .toList();
    }

    @PostMapping("/wines/{wineId}/reviews")
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeCreateWineReview
    public ReviewViewModel postReview(
        @PathVariable WineIdAttribute wineId,
        @RequestBody @Valid ReviewRequestBody reviewRequestBody
    ) {
        var author = userRepository
            .findById(UserSecurityGetter.getAuthenticatedUser().getId())
            .orElseThrow();
        var wine = wineRepository.findById(wineId.getValue()).orElseThrow(
            () -> ObjectNotFoundException.forWine().fromId(wineId.getValue())
        );

        return reviewMapper.toViewModel(reviewRepository.save(reviewMapper.buildEntity(reviewRequestBody, wine, author)));
    }
}

package ch.heig.amt.vineward.api.comment;

import ch.heig.amt.vineward.api.comment.mapping.CommentMapper;
import ch.heig.amt.vineward.api.comment.request.CommentRequestBody;
import ch.heig.amt.vineward.api.comment.viewmodel.CommentViewModel;
import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.business.repository.CommentRepository;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.security.UserSecurityGetter;
import ch.heig.amt.vineward.security.attribute.ReviewIdAttribute;
import ch.heig.amt.vineward.security.policy.AuthorizeCreateReviewComment;
import ch.heig.amt.vineward.security.policy.AuthorizeReadReviewComment;
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
 * Controller for wine review comments.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@RestController
@RequestMapping(path = "/v1/reviews/{reviewId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CommentController {

    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @GetMapping
    @Transactional(rollbackFor = Throwable.class, readOnly = true)
    @AuthorizeReadReviewComment
    public List<CommentViewModel> getCommentsForReview(
        @PathVariable ReviewIdAttribute reviewId
    ) {
        return commentRepository
            .findByReviewId(reviewId.getValue())
            .stream()
            .map(commentMapper::toViewModel)
            .toList();
    }

    @PostMapping
    @Transactional(rollbackFor = Throwable.class)
    @AuthorizeCreateReviewComment
    public void createComment(
        @PathVariable ReviewIdAttribute reviewId,
        @RequestBody @Valid CommentRequestBody requestBody
    ) {
        var author = userRepository
            .findById(UserSecurityGetter.getAuthenticatedUser().getId())
            .orElseThrow();
        var review = reviewRepository.findById(reviewId.getValue())
            .orElseThrow(() -> ObjectNotFoundException.forReview().fromId(reviewId.getValue()));

        commentRepository.save(commentMapper.toEntity(requestBody, review, author));
    }
}

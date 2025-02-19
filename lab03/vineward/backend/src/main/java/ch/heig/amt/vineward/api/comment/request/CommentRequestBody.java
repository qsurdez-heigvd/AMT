package ch.heig.amt.vineward.api.comment.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating a new comment on a review.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record CommentRequestBody(
    @NotBlank String body
) {

}

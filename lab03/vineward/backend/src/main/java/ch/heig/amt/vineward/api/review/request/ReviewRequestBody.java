package ch.heig.amt.vineward.api.review.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating a new review.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public record ReviewRequestBody(
    @NotBlank String title,
    @NotBlank String body
) {

}

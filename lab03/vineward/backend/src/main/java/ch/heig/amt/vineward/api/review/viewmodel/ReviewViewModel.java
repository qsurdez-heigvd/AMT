package ch.heig.amt.vineward.api.review.viewmodel;

import ch.heig.amt.vineward.api.reference.UserRef;
import ch.heig.amt.vineward.api.reference.WineRef;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * A view model representing a wine review.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
public final class ReviewViewModel {

    private final @NotNull Long id;
    private final @NotNull String title;
    private final @NotNull String body;
    private final @NotNull UserRef author;
    private final @NotNull WineRef wine;
}

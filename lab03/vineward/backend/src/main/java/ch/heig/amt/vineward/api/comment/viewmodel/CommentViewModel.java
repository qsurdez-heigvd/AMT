package ch.heig.amt.vineward.api.comment.viewmodel;

import ch.heig.amt.vineward.api.reference.UserRef;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * A view model representing a wine review comment.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
public final class CommentViewModel {

    private final @NotNull String body;
    private final @NotNull UserRef author;
}

package ch.heig.amt.vineward.api.wine.viewmodel;

import ch.heig.amt.vineward.api.reference.ReviewRef;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * A view model representing a wine.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
public final class WineViewModel {

    private final @NotNull Long id;
    private final @NotNull String name;
    private final @NotNull String origin;
    private final @NotNull String region;
    private final @NotNull Set<String> varieties;
    private final @NotNull String vintner;
    private final List<ReviewRef> reviews;
}

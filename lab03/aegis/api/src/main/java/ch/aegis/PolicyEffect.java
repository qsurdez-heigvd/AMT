package ch.aegis;

import java.util.Arrays;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * For a given action definition, this enumeration defines the effect that should be applied when
 * evaluating the policies defined for the action.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
@RequiredArgsConstructor
public enum PolicyEffect {
    /**
     * Indicates that all the policies defined for a given action should grant access for the action
     * to be performed.
     */
    REQUIRE_ALL(results -> {
        boolean allGranted = Arrays.stream(results).allMatch(PolicyResult::isGranted);
        return allGranted
            ? PolicyResult.granted()
            : PolicyResult.deny()
                .withCustomReason("All policies must grant access, but some denied")
                .build();
    }),

    /**
     * Indicates that at least one of the policies defined for a given action should grant access
     * for the action to be performed.
     */
    REQUIRE_ANY(results -> {
        boolean anyGranted = Arrays.stream(results).anyMatch(PolicyResult::isGranted);
        return anyGranted
            ? PolicyResult.granted()
            : PolicyResult.deny()
                .withCustomReason("At least one policy must grant access, but all denied")
                .build();
    }),

    /**
     * Indicates that only one of the policies defined for a given action should grant access for
     * the action to be performed.
     */
    REQUIRE_ONE(results -> {
        long grantedCount = Arrays.stream(results).filter(PolicyResult::isGranted).count();
        return grantedCount == 1
            ? PolicyResult.granted()
            : PolicyResult.deny().withCustomReason(
                "Exactly one policy must grant access, but %d did".formatted(grantedCount)
            ).build();
    }),

    /**
     * Indicates that none of the policies defined for a given action should grant access for the
     * action to be performed.
     */
    REQUIRE_NONE(results -> {
        boolean noneGranted = Arrays.stream(results).noneMatch(PolicyResult::isGranted);
        return noneGranted
            ? PolicyResult.granted()
            : PolicyResult.deny()
                .withCustomReason("No policies should grant access, but some did")
                .build();
    });

    @Getter(AccessLevel.PACKAGE)
    private final Function<PolicyResult[], PolicyResult> merger;
}

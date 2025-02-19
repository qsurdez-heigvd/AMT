package ch.aegis;

import ch.aegis.PolicyResult.Allow;
import ch.aegis.PolicyResult.Deny;
import ch.aegis.contract.RoleEnumerable;
import java.util.Arrays;
import lombok.Getter;

/**
 * Represents the result of an authorization policy evaluation.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public sealed interface PolicyResult permits Allow, Deny {

    /**
     * Returns whether the policy result grants access.
     *
     * @return {@code true} if the policy result grants access, {@code false} otherwise
     */
    boolean isGranted();

    /**
     * Returns a policy result that allows access.
     *
     * @return a policy result that allows access
     */
    static PolicyResult granted() {
        return Allow.INSTANCE;
    }

    /**
     * Starts building a policy result that denies access.
     *
     * @return a builder to build a policy result that denies access
     */
    static DenyBuilder deny() {
        return new DenyBuilder();
    }

    /**
     * Merges multiple policy results into a single policy result based on the specified effect.
     *
     * @param effect  the effect to apply
     * @param results the policy results to merge
     * @return the merged policy result
     */
    static PolicyResult merge(PolicyEffect effect, PolicyResult... results) {
        return effect.getMerger().apply(results);
    }

    /**
     * Represents an authorization result that allows access.
     */
    final class Allow implements PolicyResult {

        private static final Allow INSTANCE = new Allow();

        private Allow() {
        }

        @Override
        public String toString() {
            return "PolicyResult{granted}";
        }

        @Override
        public boolean isGranted() {
            return true;
        }
    }

    /**
     * Represents an authorization result that denies access.
     */
    record Deny(String justification) implements PolicyResult {

        @Override
        public String toString() {
            return "PolicyResult{denied, because='%s'}".formatted(justification);
        }

        @Override
        public boolean isGranted() {
            return false;
        }
    }

    /**
     * Builder for a policy result that denies access, allows to construct standard reason messages
     * for denying access based on the evaluation of the policy.
     */
    class DenyBuilder {

        private final StringBuilder reason = new StringBuilder();

        private DenyBuilder() {
        }

        public DenyBuilder doesNotHaveRole(RoleEnumerable<?> role) {
            reason
                .append("User does not have the required role: ").append(role)
                .append("\n");
            return this;
        }

        public DenyBuilder doesNotHaveAttribute(String attribute) {
            reason
                .append("User does not have the required attribute: ").append(attribute)
                .append("\n");
            return this;
        }

        public DenyBuilder requiredAttributeToBe(String attribute, String value) {
            reason
                .append("User attribute ").append(attribute).append(" must be ").append(value)
                .append("\n");
            return this;
        }

        public DenyBuilder requiredAttributeToBeOneOf(String attribute, String... values) {
            reason
                .append("User attribute ").append(attribute)
                .append(" must be one of: [").append(String.join(", ", values)).append("]\n");
            return this;
        }

        /**
         * Appends a custom reason to the denial message, avoid using this method if possible.
         * Standard messages offer better consistency and readability.
         *
         * @param customReason The custom reason to append
         * @return The builder instance
         */
        public DenyBuilder withCustomReason(String customReason) {
            reason.append(customReason).append("\n");
            return this;
        }

        public PolicyResult build() {
            return new Deny(reason.toString().stripTrailing());
        }
    }

    /**
     * A PolicyResult that includes context about which matcher produced the result.
     */
    final class ContextualPolicyResult {

        @Getter
        private final MatcherContext context;
        private final PolicyResult delegate;

        private ContextualPolicyResult(
            PolicyResult delegate,
            MatcherContext context
        ) {
            this.delegate = delegate;
            this.context = context;
        }

        public boolean isGranted() {
            return delegate.isGranted();
        }

        /**
         * Adds context to an existing PolicyResult.
         *
         * @param result      The original policy result
         * @param matcherName The name of the matcher that produced this result
         * @return A new PolicyResult with added context
         */
        public static ContextualPolicyResult withContext(
            PolicyResult result,
            String matcherName,
            String matcherDescription
        ) {
            return new ContextualPolicyResult(result, new MatcherContext(
                matcherName,
                matcherDescription
            ));
        }

        /**
         * Returns a formatted string representation of the policy decision and its context.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Policy decision by matcher '").append(context.matcherName()).append("': ");
            sb.append(isGranted() ? "GRANTED" : "DENIED");
            if (!context.matcherDescription().isBlank()) {
                sb.append("\nContext: ").append(context.matcherDescription());
            }

            if (delegate instanceof Deny(String justification)) {
                sb.append("\nJustification:\n").append(justification);
            }

            return sb.toString();
        }

        /**
         * Merges multiple contextual policy results while preserving their individual contexts and
         * providing a clear explanation of the merge decision.
         */
        public static ContextualPolicyResult mergeWithContext(
            String policyDefinition,
            PolicyEffect effect,
            ContextualPolicyResult... results
        ) {
            PolicyResult merged = PolicyResult.merge(
                effect,
                Arrays.stream(results)
                    .map(r -> r.delegate)
                    .toArray(PolicyResult[]::new)
            );

            StringBuilder contextDescription = new StringBuilder();
            boolean isGranted = merged.isGranted();

            // Add effect-specific explanation
            contextDescription
                .append("Combined decision (").append(effect).append("): ")
                .append(isGranted ? "GRANTED" : "DENIED").append("\n");

            contextDescription.append("Reason: ");
            if (isGranted) {
                contextDescription.append("The policy effect was satisfied");
            } else if (merged instanceof Deny(String justification)) {
                contextDescription.append(justification);
            }

            contextDescription.append("\n\nIndividual matcher results:\n");
            for (var result : results) {
                contextDescription
                    .append("- ")
                    .append(result.toString().replace("\n", "\n  "))
                    .append("\n");
            }

            return withContext(
                merged,
                policyDefinition,
                contextDescription.toString().stripTrailing()
            );
        }

        private record MatcherContext(
            String matcherName,
            String matcherDescription
        ) {

        }
    }
}

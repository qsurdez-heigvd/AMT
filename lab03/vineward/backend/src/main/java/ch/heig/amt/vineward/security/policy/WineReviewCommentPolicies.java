package ch.heig.amt.vineward.security.policy;

import ch.aegis.PolicyResult;
import ch.aegis.annotation.ActionDefinition;
import ch.aegis.annotation.ActionPolicy;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.security.attribute.ReviewAttribute;
import ch.heig.amt.vineward.security.attribute.UserCantonAttribute;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Policies for the wine review comment actions.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public interface WineReviewCommentPolicies {

    /**
     * We define this action as being:
     * <ul>
     *     <li>Everyone has access</li>
     * </ul>
     */
    @ActionDefinition
    class ReadReviewComment {

        @ActionPolicy
        PolicyResult authorizeAll() {
            return PolicyResult.granted();
        }
    }

    /**
     * We define this action as being:
     * <ul>
     *     <li>Administrators have access</li>
     *     <li>Users and reviewers can create one, as long as their canton matches the reviewed wine's</li>
     * </ul>
     */
    @ActionDefinition
    class CreateReviewComment {

        @ActionPolicy
        PolicyResult authorizeAdministrators(UserRole role) {
            if (role == UserRole.ADMINISTRATOR) {
                return PolicyResult.granted();
            }

            return PolicyResult.deny()
                .doesNotHaveRole(UserRole.ADMINISTRATOR)
                .build();
        }

        private static final Set<UserRole> ALLOWED_CREATION_ROLES = EnumSet.of(
            UserRole.USER,
            UserRole.REVIEWER
        );

        @ActionPolicy
        PolicyResult authorizeUsersFromSameCanton(
            UserRole role,
            UserCantonAttribute userCanton,
            ReviewAttribute review
        ) {
            if (!ALLOWED_CREATION_ROLES.contains(role)) {
                return PolicyResult.deny()
                    .doesNotHaveRole(UserRole.USER)
                    .build();
            }

            if (!Objects.equals(userCanton.value(), review.wine().canton())) {
                return PolicyResult.deny().requiredAttributeToBe(
                    "canton",
                    review.wine().canton().name()
                ).build();
            }

            return PolicyResult.granted();
        }
    }
}

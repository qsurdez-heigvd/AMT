package ch.heig.amt.vineward.security.policy;

import ch.aegis.PolicyResult;
import ch.aegis.annotation.ActionDefinition;
import ch.aegis.annotation.ActionPolicy;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.security.attribute.ReviewAttribute;
import ch.heig.amt.vineward.security.attribute.UserIdAttribute;
import java.util.Objects;

public interface ReviewModerationPolicies {

    /**
     * We define this action as being:
     * <ul>
     *   <li>the author of the review can submit it for moderation</li>
     * </ul>
     */
    @ActionDefinition
    class SubmitReview {

        @ActionPolicy
        PolicyResult authorizeAuthors(
            UserIdAttribute userId,
            ReviewAttribute reviewAttribute
        ) {
            if (!Objects.equals(userId, reviewAttribute.author())) {
                return PolicyResult.deny()
                    .requiredAttributeToBe("review author", "myself")
                    .build();
            }

            return PolicyResult.granted();
        }
    }

    /**
     * We define this action as being:
     * <ul>
     *   <li>administrators and moderators can moderate</li>
     * </ul>
     */
    @ActionDefinition
    class InspectReview {

        @ActionPolicy(description = "Only admin and moderators can inspect, approve and deny a review")
        PolicyResult authorizeModerators(UserRole role) {
            if (role == UserRole.ADMINISTRATOR || role == UserRole.MODERATOR) {
                return PolicyResult.granted();
            }

            return PolicyResult
                .deny()
                .doesNotHaveRole(UserRole.MODERATOR)
                .build();
        }
    }

}

package ch.heig.amt.vineward.security.policy;

import ch.aegis.PolicyResult;
import ch.aegis.annotation.ActionDefinition;
import ch.aegis.annotation.ActionPolicy;
import ch.heig.amt.vineward.business.model.Region;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.security.attribute.UserCantonAttribute;
import ch.heig.amt.vineward.security.attribute.WineAttribute;

/**
 * Policies for the wine review actions.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
interface WineReviewPolicies {

    /**
     * We define this action as being:
     * <ul>
     *     <li>Everyone has access</li>
     * </ul>
     */
    @ActionDefinition
    class ReadWineReview {

        @ActionPolicy
        PolicyResult authorizeAll() {
            return PolicyResult.granted();
        }
    }

    /**
     * We define this action as being:
     * <ul>
     *     <li>Administrators have access</li>
     *     <li>Reviewers can create one, so long as their canton of origin does not conflict with the wine canton</li>
     * </ul>
     */
    @ActionDefinition
    class CreateWineReview {

        @ActionPolicy
        PolicyResult authorizeAdministrators(UserRole role) {
            if (role == UserRole.ADMINISTRATOR) {
                return PolicyResult.granted();
            }

            return PolicyResult.deny()
                .doesNotHaveRole(UserRole.ADMINISTRATOR)
                .build();
        }

        @ActionPolicy(description = "Reviewers can only review wines from non-conflictual cantons")
        PolicyResult authorizeReviewers(
            UserRole role,
            UserCantonAttribute userCanton,
            WineAttribute wine
        ) {
            if (role != UserRole.REVIEWER || userCanton.value() == null) {
                return PolicyResult.deny()
                    .doesNotHaveRole(UserRole.REVIEWER)
                    .build();
            }

            if (!userCanton.value().getAllowableRegions().contains(wine.region())) {
                return PolicyResult.deny()
                    .requiredAttributeToBeOneOf(
                        "allowed regions",
                        userCanton.value().getAllowableRegions().stream()
                            .map(Region::name)
                            .toArray(String[]::new)
                    )
                    .build();
            }

            return PolicyResult.granted();
        }
    }
}

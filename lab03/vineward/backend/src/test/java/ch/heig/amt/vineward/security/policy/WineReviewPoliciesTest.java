package ch.heig.amt.vineward.security.policy;

import static ch.aegis.test.PolicyResultAssert.assertThat;

import ch.heig.amt.vineward.business.model.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WineReviewPoliciesTest {

    @Nested
    @DisplayName("ReadWineReview")
    class ReadWineReviewTests {

        private final WineReviewPolicies.ReadWineReview policies = new WineReviewPolicies.ReadWineReview();

        @Test
        @DisplayName("authorizeAll should always grant access")
        void authorizeAll_shouldAlwaysGrantAccess() {
            var result = policies.authorizeAll();

            assertThat(result).isGranted();
        }
    }

    @Nested
    @DisplayName("CreateWineReview")
    class CreateWineReviewTests {

        private final WineReviewPolicies.CreateWineReview policies = new WineReviewPolicies.CreateWineReview();

        @Test
        @DisplayName("authorizeAdministrators should grant access to administrators")
        void authorizeAdministrators_withAdministratorRole_shouldGrantAccess() {
            var result = policies.authorizeAdministrators(UserRole.ADMINISTRATOR);

            assertThat(result).isGranted();
        }

        @Test
        @DisplayName("authorizeAdministrators should deny access to non-administrators")
        void authorizeAdministrators_withNonAdministratorRole_shouldDenyAccess() {
            var result = policies.authorizeAdministrators(UserRole.REVIEWER);

            assertThat(result).isDenied()
                .hasDenialJustification("does not have the required role: ADMINISTRATOR");
        }
    }
}

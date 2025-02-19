package ch.aegis.test;

import ch.aegis.PolicyResult;
import ch.aegis.PolicyResult.Allow;
import ch.aegis.PolicyResult.Deny;
import org.assertj.core.api.AbstractAssert;

/**
 * Fluent assertions based on AssertJ for {@link PolicyResult}.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 * @since 1.0
 */
public final class PolicyResultAssert extends AbstractAssert<PolicyResultAssert, PolicyResult> {

    public PolicyResultAssert(PolicyResult actual) {
        super(actual, PolicyResultAssert.class);
    }

    public static PolicyResultAssert assertThat(PolicyResult actual) {
        return new PolicyResultAssert(actual);
    }

    /**
     * Asserts that the policy result is an Allow result.
     */
    public PolicyResultAssert isAllow() {
        isNotNull();
        if (!(actual instanceof Allow)) {
            failWithMessage("Expected policy result to be Allow but was %s", actual);
        }
        return this;
    }

    /**
     * Asserts that the policy result is a Deny result.
     */
    public PolicyResultAssert isDeny() {
        isNotNull();
        if (!(actual instanceof Deny)) {
            failWithMessage("Expected policy result to be Deny but was %s", actual);
        }
        return this;
    }

    /**
     * Asserts that the policy result grants access.
     */
    public PolicyResultAssert isGranted() {
        isNotNull();
        if (!actual.isGranted()) {
            failWithMessage("Expected policy result to be granted but was denied");
        }
        return this;
    }

    /**
     * Asserts that the policy result denies access.
     */
    public PolicyResultAssert isDenied() {
        isNotNull();
        if (actual.isGranted()) {
            failWithMessage("Expected policy result to be denied but was granted");
        }
        return this;
    }

    /**
     * Asserts that the denial justification contains the expected text.
     */
    public PolicyResultAssert hasDenialJustification(String expectedJustification) {
        isDeny();
        Deny deny = (Deny) actual;
        if (!deny.justification().contains(expectedJustification)) {
            failWithMessage("Expected denial justification to contain <%s> but was <%s>",
                expectedJustification, deny.justification());
        }
        return this;
    }
}

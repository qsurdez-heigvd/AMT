package ch.heig.amt.vineward.security.policy;

import ch.aegis.PolicyResult;
import ch.aegis.annotation.ActionDefinition;
import ch.aegis.annotation.ActionPolicy;

/**
 * Policies for wine access.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public interface WinePolicies {

    /**
     * We define this action as being:
     * <ul>
     *     <li>Everyone has access</li>
     * </ul>
     */
    @ActionDefinition
    class ReadWine {

        @ActionPolicy
        PolicyResult authorizeAll() {
            return PolicyResult.granted();
        }
    }
}

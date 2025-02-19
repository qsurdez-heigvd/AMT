package ch.heig.amt.vineward.api.security.viewmodel;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * View model for an authentication.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
@Jacksonized
public final class AuthenticationViewModel {

    private final String accessToken;
    private final String refreshToken;
}

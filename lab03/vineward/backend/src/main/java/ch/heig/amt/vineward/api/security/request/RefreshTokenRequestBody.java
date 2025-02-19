package ch.heig.amt.vineward.api.security.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request body for requesting a token refresh.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
@Jacksonized
public class RefreshTokenRequestBody {

    private final String refreshToken;
}

package ch.heig.amt.vineward.configuration.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for JWT user authentication.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@ConfigurationProperties("vineward.security.jwt")
public record JwtSecurityProperties(
    String secretKey,
    int expiration,
    RefreshToken refreshToken
) {

    public record RefreshToken(
        int expiration
    ) {
    }
}

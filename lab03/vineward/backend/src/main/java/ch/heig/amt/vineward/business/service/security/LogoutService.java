package ch.heig.amt.vineward.business.service.security;

import ch.heig.amt.vineward.business.model.user.UserToken;
import ch.heig.amt.vineward.business.repository.UserTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling the logout of a user.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UserTokenRepository tokenRepository;

    @Override
    @Transactional
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        var jwtToken = authHeader.substring(7);
        tokenRepository.findByToken(jwtToken).ifPresent(UserToken::revoke);
    }
}

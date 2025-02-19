package ch.heig.amt.vineward.business.service.security;

import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.api.exception.TokenExpiredException;
import ch.heig.amt.vineward.api.exception.UsernameAlreadyExistsException;
import ch.heig.amt.vineward.api.security.request.LoginRequestBody;
import ch.heig.amt.vineward.api.security.request.RefreshTokenRequestBody;
import ch.heig.amt.vineward.api.security.request.RegisterRequestBody;
import ch.heig.amt.vineward.api.security.viewmodel.AuthenticationViewModel;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.business.model.user.UserToken;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.business.repository.UserTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service managing JWT operations and user authentication.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserTokenRepository tokenRepository;

    public AuthenticationViewModel login(LoginRequestBody request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> ObjectNotFoundException.forUser().fromEmail(request.getEmail()));

        return saveUserTokenAndReturnAuthResponse(user);
    }

    public void register(RegisterRequestBody request) {
        if (userRepository.existsByEmail(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        userRepository.save(buildUser(request));
    }

    public AuthenticationViewModel refreshToken(RefreshTokenRequestBody request) {
        var refreshToken = request.getRefreshToken();
        try {
            var userEmail = jwtService.extractUsername(refreshToken);
            var user = userRepository.findByEmail(userEmail).orElseThrow();

            var isRefreshTokenValid = jwtService.isTokenValid(refreshToken, user);
            if (!isRefreshTokenValid) {
                refreshToken = jwtService.generateRefreshToken(user);
            }

            String accessToken = jwtService.generateToken(user);
            user.addToken(
                new UserToken()
                    .setToken(accessToken)
                    .setRefreshToken(refreshToken)
            );

            return AuthenticationViewModel.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        }
    }

    private User buildUser(RegisterRequestBody request) {
        return new User()
            .setEmail(request.getEmail())
            .setDisplayName(request.getUsername())
            .setPassword(passwordEncoder.encode(request.getPassword()))
            .setAuthorities(EnumSet.of(UserRole.USER))
            .setOrigin(request.getCanton());
    }

    private AuthenticationViewModel saveUserTokenAndReturnAuthResponse(User user) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.addToken(
            new UserToken()
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
        );

        return AuthenticationViewModel.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }
}

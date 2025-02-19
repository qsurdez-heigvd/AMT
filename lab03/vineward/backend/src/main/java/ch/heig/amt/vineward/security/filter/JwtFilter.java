package ch.heig.amt.vineward.security.filter;

import ch.heig.amt.vineward.business.repository.UserTokenRepository;
import ch.heig.amt.vineward.business.service.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Spring authentication filter for extracting the user metadata from the JWT token and setting the
 * user in the security context.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserTokenRepository userTokenRepository;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var requestPath = request.getServletPath();
        var isRefresh = requestPath.equals("/v1/auth/refresh-token");
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = authHeader.substring(7);

        String userEmail = null;
        try {
            userEmail = jwtService.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            if (!isRefresh) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (Objects.nonNull(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = userTokenRepository.findByToken(jwtToken)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

            if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null,
                    userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

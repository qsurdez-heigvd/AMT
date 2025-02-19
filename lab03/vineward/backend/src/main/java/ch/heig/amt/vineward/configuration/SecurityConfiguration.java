package ch.heig.amt.vineward.configuration;

import ch.aegis.annotation.EnableAegisSpringWebSecurity;
import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.configuration.model.JwtSecurityProperties;
import ch.heig.amt.vineward.security.filter.JwtFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration for JWT user authentication.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableAegisSpringWebSecurity
@EnableConfigurationProperties(JwtSecurityProperties.class)
public class SecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> ObjectNotFoundException.forUser().fromEmail(username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 4, 1 << 16, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("authorization", "content-type"));
        configuration.setExposedHeaders(List.of());
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity httpConfigurer,
        JwtFilter jwtFilter,
        AuthenticationProvider authenticationProvider,
        LogoutHandler logoutHandler
    ) throws Exception {
        httpConfigurer
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    .requestMatchers("/v1/auth/@me").authenticated()
                    .requestMatchers("/v1/auth/**").permitAll()
                    .requestMatchers("/images/**").permitAll()
                    .requestMatchers(
                        "/swagger-ui/**", "/swagger-ui.html",
                        "/api-docs", "/api-docs/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(
                logout -> logout
                    .logoutUrl("/api/v1/auth/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler((request, response, authentication) -> {
                        SecurityContextHolder.clearContext();
                    })
            );
        return httpConfigurer.build();
    }
}

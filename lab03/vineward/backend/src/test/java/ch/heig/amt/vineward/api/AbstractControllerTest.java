package ch.heig.amt.vineward.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import ch.heig.amt.vineward.AbstractCommonTest;
import ch.heig.amt.vineward.AegisWebMvcTest;
import ch.heig.amt.vineward.api.AbstractControllerTest.ControllerContextConfiguration;
import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.security.filter.JwtFilter;
import jakarta.servlet.FilterChain;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * This class serves as the base context configuration for controller unit tests, where only the
 * mappers are expected to be defined in the context. Usage:
 *
 * <pre>{@code
 * @WebMvcTest(MyController.class)
 * class MyControllerTest extends AbstractControllerTest {
 *
 *   // [...]
 * }
 * }</pre>
 */
@AegisWebMvcTest
@Import(ControllerContextConfiguration.class)
public abstract class AbstractControllerTest extends AbstractCommonTest {

    @ComponentScan(
        basePackages = "ch.heig.amt.vineward.api",
        includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*MapperImpl"),
        useDefaultFilters = false
    )
    static class ControllerContextConfiguration {

        @Bean
        private UserDetailsService userDetailsService() {
            return (username) -> new User()
                .setEmail("%s@vineward.ch".formatted(username))
                .setDisplayName(username)
                .setOrigin(Canton.VAUD)
                .setAuthorities(Set.of(UserRole.USER));
        }
    }

    @Autowired
    protected MockMvc api;

    protected @MockitoBean JwtFilter jwtFilter;

    @BeforeEach
    void setupJwtFilter() throws Exception {
        doAnswer(invocation -> {
            var chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }
}

package ch.heig.amt.vineward.api.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.heig.amt.vineward.AbstractCommonTest;
import ch.heig.amt.vineward.TestcontainersConfiguration;
import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.Region;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.model.user.UserRole;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.business.repository.WineRepository;
import ch.heig.amt.vineward.security.filter.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class WineReviewControllerIntegrationTest extends AbstractCommonTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WineRepository wineRepository;

    @MockitoSpyBean
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    private Wine vaudWine;
    private Wine valaisWine;

    private User adminUser;
    private User vaudReviewer;
    private User fribourgReviewer;

    @BeforeEach
    void setup() throws Exception {
        // Create test wines in different regions
        vaudWine = wineRepository.save(new Wine()
            .setName("Vaud Wine")
            .setVintner("Test Vintner")
            .setOrigin(Canton.VAUD)
            .setRegion(Region.VAUD)
            .setVarieties(Set.of()));

        valaisWine = wineRepository.save(new Wine()
            .setName("Valais Wine")
            .setVintner("Test Vintner")
            .setOrigin(Canton.WALLIS)
            .setRegion(Region.VALAIS)
            .setVarieties(Set.of()));

        // Create and save users
        adminUser = userRepository.save(new User()
            .setEmail("test_admin@vineward.ch")
            .setDisplayName("admin")
            .setPassword("password")
            .setAuthorities(EnumSet.of(UserRole.ADMINISTRATOR))
            .setOrigin(Canton.ZURICH));

        vaudReviewer = userRepository.save(new User()
            .setEmail("vaud_reviewer@vineward.ch")
            .setDisplayName("vaud_reviewer")
            .setAuthorities(EnumSet.of(UserRole.REVIEWER))
            .setPassword("password")
            .setOrigin(Canton.VAUD));

        fribourgReviewer = userRepository.save(new User()
            .setEmail("fribourg_reviewer@vineward.ch")
            .setDisplayName("fribourg_reviewer")
            .setPassword("password")
            .setAuthorities(EnumSet.of(UserRole.REVIEWER))
            .setOrigin(Canton.FRIBOURG));

        doAnswer(invocation -> {
            var chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }

    @Nested
    class CreateReview {

        private String createReviewRequest(String title, String body) throws Exception {
            record ReviewRequest(String title, String body) {

            }

            return objectMapper.writeValueAsString(new ReviewRequest(title, body));
        }

        @Test
        void whenUserIsAdmin_shouldCreateReviewForAnyRegion() throws Exception {
            // Try to create reviews for wines from different regions
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", vaudWine.getId())
                    .with(csrf())
                    .with(user(adminUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Vaud Review", "Great wine!")))
                .andExpect(status().isOk());

            mockMvc.perform(post("/v1/wines/{wineId}/reviews", valaisWine.getId())
                    .with(csrf())
                    .with(user(adminUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Valais Review", "Also great!")))
                .andExpect(status().isOk());

            // Verify reviews were created
            verify(reviewRepository, times(2)).save(any());
        }

        @Test
        void whenUserIsReviewer_shouldOnlyCreateReviewForAllowedRegions() throws Exception {
            // Should succeed for Vaud wine
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", vaudWine.getId())
                    .with(csrf())
                    .with(user(vaudReviewer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Vaud Review", "Good wine!")))
                .andExpect(status().isOk());

            // Should fail for Valais wine (conflicting region)
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", valaisWine.getId())
                    .with(csrf())
                    .with(user(vaudReviewer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Valais Review", "Nice wine!")))
                .andExpect(status().isForbidden());

            // Verify only one review was created
            verify(reviewRepository, times(1)).save(argThat(
                review -> review.getWine().getRegion() == Region.VAUD
                    && review.getAuthor().getOrigin() == Canton.VAUD
                    && review.getStatus() == ReviewStatus.DRAFT
            ));
        }

        @Test
        void whenUserIsFribourgReviewer_shouldCreateReviewForMultipleAllowedRegions()
            throws Exception {
            // Should succeed for Vaud wine (allowed region)
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", vaudWine.getId())
                    .with(csrf())
                    .with(user(fribourgReviewer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Vaud Review", "Excellent!")))
                .andExpect(status().isOk());

            // Should fail for Valais wine (not in allowed regions)
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", valaisWine.getId())
                    .with(csrf())
                    .with(user(fribourgReviewer))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Valais Review", "Also good!")))
                .andExpect(status().isForbidden());

            // Verify results
            verify(reviewRepository, times(1)).save(argThat(
                review -> review.getWine().getRegion() == Region.VAUD
                    && review.getAuthor().getOrigin() == Canton.FRIBOURG
                    && review.getStatus() == ReviewStatus.DRAFT
            ));
        }

        @Test
        void whenUserIsNotAuthenticated_shouldReturnUnauthorized() throws Exception {
            mockMvc.perform(post("/v1/wines/{wineId}/reviews", vaudWine.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createReviewRequest("Review", "Content")))
                .andExpect(status().isForbidden());

            verify(reviewRepository, never()).save(any());
        }
    }
}

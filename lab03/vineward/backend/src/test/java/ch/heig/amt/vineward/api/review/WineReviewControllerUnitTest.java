package ch.heig.amt.vineward.api.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.aegis.security.AegisAction;
import ch.aegis.test.spring.security.WithUserAction;
import ch.heig.amt.vineward.AegisWebMvcTest;
import ch.heig.amt.vineward.api.AbstractControllerTest;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.repository.UserRepository;
import ch.heig.amt.vineward.business.repository.WineRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@AegisWebMvcTest(WineReviewController.class)
class WineReviewControllerUnitTest extends AbstractControllerTest {

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private WineRepository wineRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
    }

    @Nested
    class GetAllReviews {
        @Test
        @WithUserAction(action = AegisAction.READ_WINE_REVIEW)
        void getAllReviews_whenUserIsAuthorized_shouldSucceed() throws Exception {
            var review = new Review();

            when(reviewRepository.findAll()).thenReturn(List.of(review));

            api.perform(get("/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithUserAction(action = AegisAction.READ_WINE_REVIEW, authorized = false)
        void getAllReviews_whenUserIsNotAuthorized_shouldReturnUnauthorized() throws Exception {
            api.perform(get("/v1/reviews"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class GetWineReviews {
        @Test
        @WithUserAction(action = AegisAction.READ_WINE_REVIEW)
        void getWineReviews_whenUserIsAuthorized_shouldSucceed() throws Exception {
            var wineId = 123L;
            var review = new Review();

            when(reviewRepository.findById(wineId)).thenReturn(Optional.of(review));

            api.perform(get("/v1/wines/{wineId}/reviews", wineId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithUserAction(action = AegisAction.READ_WINE_REVIEW, authorized = false)
        void getWineReviews_whenUserIsNotAuthorized_shouldReturnUnauthorized() throws Exception {
            var wineId = "123";
            api.perform(get("/v1/wines/{wineId}/reviews", wineId))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class CreateWineReview {
        @Test
        @WithUserAction(action = AegisAction.CREATE_WINE_REVIEW)
        void createReview_whenUserIsAuthorized_shouldSucceed() throws Exception {
            var wineId = 123L;
            var wine = new Wine();

            when(wineRepository.findById(wineId)).thenReturn(Optional.of(wine));

            api.perform(post("/v1/wines/{wineId}/reviews", wineId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"title\",\"body\":\"body\"}"))
                .andExpect(status().isOk());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_WINE_REVIEW, authorized = false)
        void createReview_whenUserIsNotAuthorized_shouldReturnUnauthorized() throws Exception {
            var wineId = "123";
            api.perform(post("/v1/wines/{wineId}/reviews", wineId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"title\",\"body\":\"body\"}"))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_WINE_REVIEW)
        void createReview_withEmptyRequestBody_shouldReturnBadRequest() throws Exception {
            var wineId = 123L;
            when(wineRepository.findById(wineId)).thenReturn(Optional.of(new Wine()));
            api.perform(post("/v1/wines/{wineId}/reviews", wineId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_WINE_REVIEW)
        void createReview_whenWineDoesNotExist_shouldReturnNotFound() throws Exception {
            var wineId = 123L;
            when(wineRepository.findById(wineId)).thenReturn(Optional.empty());

            api.perform(post("/v1/wines/{wineId}/reviews", wineId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"title\",\"body\":\"body\"}"))
                .andExpect(status().isNotFound());
        }
    }
}

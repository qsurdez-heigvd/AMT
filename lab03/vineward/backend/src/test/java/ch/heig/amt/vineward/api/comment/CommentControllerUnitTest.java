package ch.heig.amt.vineward.api.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.aegis.security.AegisAction;
import ch.aegis.test.spring.security.WithUserAction;
import ch.heig.amt.vineward.AegisWebMvcTest;
import ch.heig.amt.vineward.api.AbstractControllerTest;
import ch.heig.amt.vineward.business.model.review.Comment;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import ch.heig.amt.vineward.business.repository.CommentRepository;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.business.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@AegisWebMvcTest(CommentController.class)
class CommentControllerUnitTest extends AbstractControllerTest {

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
    }

    @Nested
    class GetReviewComments {
        @Test
        @WithUserAction(action = AegisAction.READ_REVIEW_COMMENT)
        void getComments_whenUserIsAuthorized_shouldSucceed() throws Exception {
            var reviewId = 123L;
            var comment = new Comment();
            comment.setBody("comment");
            comment.setAuthor(new User().setDisplayName("user"));

            when(commentRepository.findByReviewId(reviewId)).thenReturn(List.of(comment));

            api.perform(get("/v1/reviews/{reviewId}/comments", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author.displayName").value("user"))
                .andExpect(jsonPath("$[0].body").value("comment"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @WithUserAction(action = AegisAction.READ_REVIEW_COMMENT, authorized = false)
        void getComments_whenUserIsNotAuthorized_shouldReturnUnauthorized() throws Exception {
            var reviewId = 123L;
            api.perform(get("/v1/reviews/{reviewId}/comments", reviewId))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    class CreateReviewComment {
        @Test
        @WithUserAction(action = AegisAction.CREATE_REVIEW_COMMENT)
        void createComment_whenUserIsAuthorized_shouldSucceed() throws Exception {
            var reviewId = 123L;
            var review = new Review();

            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            api.perform(post("/v1/reviews/{reviewId}/comments", reviewId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"body\":\"comment\"}"))
                .andExpect(status().isOk());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_REVIEW_COMMENT, authorized = false)
        void createComment_whenUserIsNotAuthorized_shouldReturnUnauthorized() throws Exception {
            var reviewId = 123L;
            api.perform(post("/v1/reviews/{reviewId}/comments", reviewId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"body\":\"comment\"}"))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_REVIEW_COMMENT)
        void createComment_whenReviewDoesNotExist_shouldReturnNotFound() throws Exception {
            var reviewId = 123L;
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            api.perform(post("/v1/reviews/{reviewId}/comments", reviewId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"body\":\"comment\"}"))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithUserAction(action = AegisAction.CREATE_REVIEW_COMMENT)
        void createComment_whenRequestBodyIsInvalid_shouldReturnBadRequest() throws Exception {
            var reviewId = 123L;
            var invalidJson = "{invalid:}";

            api.perform(post("/v1/reviews/{reviewId}/comments", reviewId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                .andExpect(status().isBadRequest());
        }
    }
}

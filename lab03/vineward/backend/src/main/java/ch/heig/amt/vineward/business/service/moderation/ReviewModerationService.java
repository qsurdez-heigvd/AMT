package ch.heig.amt.vineward.business.service.moderation;

import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.review.ReviewStatus;
import ch.heig.amt.vineward.business.service.moderation.model.PendingReviewMapper;
import ch.heig.amt.vineward.business.service.moderation.model.PendingReviewModel;
import ch.heig.amt.vineward.configuration.JmsConfig;
import jakarta.jms.Message;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Moderation service interacting with a JMS queue to list pending moderation actions.
 * <p>
 * Methods in this service expect an existing transaction to be active.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Service
@RequiredArgsConstructor
public class ReviewModerationService {

    private final JmsTemplate jmsTemplate;
    private final PendingReviewMapper pendingReviewMapper;

    /**
     * Submits a review for moderation by adding it to the JMS queue
     */
    public void submitForModeration(Review review) {
        review.setStatus(ReviewStatus.PENDING_REVIEW);
        jmsTemplate.convertAndSend(
            JmsConfig.REVIEW_QUEUE,
            pendingReviewMapper.toMessageModel(review),
            msg -> {
                msg.setJMSCorrelationID(review.getId().toString());
                return msg;
            }
        );
    }

    /**
     * Peeks at the first review in the moderation queue without removing it
     */
    public Optional<PendingReviewModel> peekNextReview() {
        return jmsTemplate.browse(((session, browser) -> {
            var messages = browser.getEnumeration();
            if (!messages.hasMoreElements()) {
                return Optional.empty();
            }

            Message message = (Message) messages.nextElement();
            return Optional.of(
                (PendingReviewModel) jmsTemplate.getMessageConverter().fromMessage(message)
            );
        }));
    }

    /**
     * Approves a review, marking it as verified
     */
    public void approveReview(Review review) {
        jmsTemplate.receiveSelectedAndConvert(
            JmsConfig.REVIEW_QUEUE,
            "JMSCorrelationID = '%d'".formatted(review.getId())
        );

        review.setStatus(ReviewStatus.VERIFIED);
    }

    /**
     * Rejects a review, removing it from the queue and db
     */
    public void declineReview(Review review) {
        jmsTemplate.receiveSelectedAndConvert(
            JmsConfig.REVIEW_QUEUE,
            "JMSCorrelationID = '%d'".formatted(review.getId())
        );
    }
}

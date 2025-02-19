package ch.heig.amt.vineward.business.service.moderation.model;

import ch.heig.amt.vineward.api.reference.ReferenceMapper;
import ch.heig.amt.vineward.business.model.review.Review;
import org.mapstruct.Mapper;

/**
 * Mapper for converting the business entity to a messaging model for pending reviews.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper(uses = {ReferenceMapper.class})
public interface PendingReviewMapper {

    PendingReviewModel toMessageModel(Review review);
}

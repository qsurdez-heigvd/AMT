package ch.heig.amt.vineward.api.review.mapping;

import ch.heig.amt.vineward.api.reference.ReferenceMapper;
import ch.heig.amt.vineward.api.review.request.ReviewRequestBody;
import ch.heig.amt.vineward.api.review.viewmodel.ReviewViewModel;
import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapping interface for review entities.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper(uses = {ReferenceMapper.class})
public interface WineReviewMapper {

    ReviewViewModel toViewModel(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    Review buildEntity(ReviewRequestBody reviewRequestBody, Wine wine, User author);
}

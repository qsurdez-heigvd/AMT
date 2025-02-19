package ch.heig.amt.vineward.api.reference;

import ch.heig.amt.vineward.business.model.Wine;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import org.mapstruct.Mapper;

/**
 * Common mapper for reference types. Reference types help implement references in the generated
 * view models to avoid pulling in the full object graph. These should in theory represent enough
 * information to be displayable in a link to the full object.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper
public interface ReferenceMapper {

    ReviewRef toRef(Review review);

    UserRef toRef(User user);

    WineRef toRef(Wine wine);
}

package ch.heig.amt.vineward.security.resolver;

import ch.aegis.contract.AttributeResolver;
import ch.heig.amt.vineward.api.exception.ObjectNotFoundException;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.repository.ReviewRepository;
import ch.heig.amt.vineward.security.attribute.ReviewAttribute;
import ch.heig.amt.vineward.security.attribute.ReviewIdAttribute;
import ch.heig.amt.vineward.security.attribute.UserIdAttribute;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * Resolver for the {@link ReviewIdAttribute} to {@link ReviewAttribute} conversion, fetching the
 * review from the database by its ID and mapping it to an attribute for policy evaluation.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Component
@RequiredArgsConstructor
public final class ReviewIdReviewResolver implements
    AttributeResolver<ReviewIdAttribute, ReviewAttribute> {

    private final AttributeMapper attributeMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public Class<ReviewIdAttribute> getSourceAttributeType() {
        return ReviewIdAttribute.class;
    }

    @Override
    public Class<ReviewAttribute> getResolvedAttributeType() {
        return ReviewAttribute.class;
    }

    @Override
    public ReviewAttribute resolve(ReviewIdAttribute sourceAttribute) {
        return reviewRepository.findById(sourceAttribute.getValue())
            .map(attributeMapper::toAttribute)
            .orElseThrow(
                () -> ObjectNotFoundException
                    .forReview()
                    .fromId(sourceAttribute.getValue())
            );
    }

    @Mapper(uses = WineIdWineResolver.AttributeMapper.class)
    interface AttributeMapper {

        @Mapping(target = "author", source = "author.id")
        ReviewAttribute toAttribute(Review source);

        static UserIdAttribute map(UUID source) {
            return new UserIdAttribute(source);
        }
    }
}

package ch.heig.amt.vineward.api.comment.mapping;

import ch.heig.amt.vineward.api.comment.request.CommentRequestBody;
import ch.heig.amt.vineward.api.comment.viewmodel.CommentViewModel;
import ch.heig.amt.vineward.api.reference.ReferenceMapper;
import ch.heig.amt.vineward.business.model.review.Comment;
import ch.heig.amt.vineward.business.model.review.Review;
import ch.heig.amt.vineward.business.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapping interface for review comment entities.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper(uses = {ReferenceMapper.class})
public interface CommentMapper {

    CommentViewModel toViewModel(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "body", source = "requestBody.body")
    @Mapping(target = "review", source = "review")
    @Mapping(target = "author", source = "author")
    Comment toEntity(CommentRequestBody requestBody, Review review, User author);
}

package ch.heig.amt.vineward.api.security.mapping;

import ch.heig.amt.vineward.api.security.viewmodel.UserViewModel;
import ch.heig.amt.vineward.business.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapping interface for user entities.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Mapper
public interface UserMapper {

    @Mapping(target = "roles", source = "authorities")
    @Mapping(target = "username", source = "displayName")
    UserViewModel toViewModel(User user);
}

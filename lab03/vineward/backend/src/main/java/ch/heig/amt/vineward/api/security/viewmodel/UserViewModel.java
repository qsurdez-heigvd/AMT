package ch.heig.amt.vineward.api.security.viewmodel;

import ch.heig.amt.vineward.business.model.Canton;
import ch.heig.amt.vineward.business.model.user.UserRole;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * View model representing user details.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
public final class UserViewModel {

    private final String username;
    private final String email;
    private final List<UserRole> roles;
    private final Canton origin;
}

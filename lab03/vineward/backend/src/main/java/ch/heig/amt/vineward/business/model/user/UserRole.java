package ch.heig.amt.vineward.business.model.user;

import ch.aegis.annotation.RoleDefinition;
import ch.aegis.contract.RoleEnumerable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Role definition for VineWard users.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
@RoleDefinition
@RequiredArgsConstructor
public enum UserRole implements RoleEnumerable<UserRole> {
    ADMINISTRATOR("Administrator"),
    MODERATOR("Moderator"),
    REVIEWER("Reviewer"),
    USER("User");

    private final String displayName;
}

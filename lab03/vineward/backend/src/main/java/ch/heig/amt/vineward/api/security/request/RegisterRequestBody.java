package ch.heig.amt.vineward.api.security.request;

import ch.heig.amt.vineward.business.model.Canton;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Request body for user registration.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Data
@Builder
@Jacksonized
public class RegisterRequestBody {

    private final @NotBlank @Email String email;
    private final @NotBlank String username;
    private final @ToString.Exclude @NotBlank String password;
    private final @NotNull Canton canton;
}

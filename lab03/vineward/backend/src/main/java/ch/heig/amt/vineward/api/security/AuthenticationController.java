package ch.heig.amt.vineward.api.security;

import ch.heig.amt.vineward.api.security.mapping.UserMapper;
import ch.heig.amt.vineward.api.security.request.LoginRequestBody;
import ch.heig.amt.vineward.api.security.request.RefreshTokenRequestBody;
import ch.heig.amt.vineward.api.security.request.RegisterRequestBody;
import ch.heig.amt.vineward.api.security.viewmodel.AuthenticationViewModel;
import ch.heig.amt.vineward.api.security.viewmodel.UserViewModel;
import ch.heig.amt.vineward.business.service.security.AuthenticationService;
import ch.heig.amt.vineward.security.UserSecurityGetter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@RestController
@RequestMapping(path = "/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @GetMapping("/@me")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<UserViewModel> getUser() {
        return ResponseEntity.ok(userMapper.toViewModel(
            UserSecurityGetter.getAuthenticatedUser()
        ));
    }

    @PostMapping("/login")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<AuthenticationViewModel> login(
        @Valid @RequestBody LoginRequestBody request
    ) {
        return ResponseEntity.ok(
            authenticationService.login(request)
        );
    }

    @PostMapping("/register")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Void> register(
        @Valid @RequestBody RegisterRequestBody request
    ) {
        authenticationService.register(request);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<AuthenticationViewModel> refreshToken(
        @Valid @RequestBody RefreshTokenRequestBody request
    ) {
        return new ResponseEntity<>(
            authenticationService.refreshToken(request),
            HttpStatus.CREATED
        );
    }
}

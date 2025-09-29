package hello.pet.authservice.adapter.in.web;

import hello.pet.authservice.adapter.in.web.dto.LoginRequest;
import hello.pet.authservice.adapter.out.dto.LoginResponse;
import hello.pet.authservice.adapter.out.security.TokenHash;
import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.LogoutUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.in.command.LogoutCommand;
import hello.pet.authservice.application.port.out.result.LoginResult;
import hello.pet.authservice.application.port.out.result.LogoutResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${jwt.access-expiration}")
    private int jwtAccessExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private int jwtRefreshTokenExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req,
                                               HttpServletResponse resp) {
        LoginCommand cmd = req.toCommand(req);
        LoginResult res = loginUseCase.login(cmd);
        LoginResponse response = LoginResponse.from(res);

        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", res.accessToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMillis(jwtAccessExpirationMs))
                .sameSite(cookieSameSite)
                .build();

        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", res.refreshToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMillis(jwtRefreshTokenExpirationMs))
                .sameSite(cookieSameSite)
                .build();

        resp.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse resp) {

        LogoutCommand cmd = new LogoutCommand(userId, TokenHash.sha256(refreshToken));
        LogoutResult res = logoutUseCase.logout(cmd);

        if (!res.success()) {
            log.warn("Logout failed for user: {}, reason: {}", userId, res.message());
        }

        ResponseCookie access = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        ResponseCookie refresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        resp.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

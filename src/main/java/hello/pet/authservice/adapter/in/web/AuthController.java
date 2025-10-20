package hello.pet.authservice.adapter.in.web;

import hello.pet.authservice.adapter.in.web.config.CookieFactory;
import hello.pet.authservice.adapter.in.web.dto.LoginRequest;
import hello.pet.authservice.adapter.in.web.dto.RefreshTokenResponse;
import hello.pet.authservice.adapter.out.dto.LoginResponse;
import hello.pet.authservice.adapter.out.security.TokenHash;
import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.LogoutUseCase;
import hello.pet.authservice.application.port.in.RefreshTokenUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.in.command.LogoutCommand;
import hello.pet.authservice.application.port.in.command.RefreshCommand;
import hello.pet.authservice.application.port.out.result.LoginResult;
import hello.pet.authservice.application.port.out.result.LogoutResult;
import hello.pet.authservice.application.port.out.result.RefreshResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final CookieFactory cookieFactory;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req,
                                               HttpServletResponse resp) {
        LoginCommand cmd = req.toCommand(req);
        LoginResult res = loginUseCase.login(cmd);
        LoginResponse response = LoginResponse.from(res);

        ResponseCookie access = cookieFactory.createAccessTokenCookie(res.accessToken());
        ResponseCookie refresh = cookieFactory.createRefreshTokenCookie(res.refreshToken());

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

        ResponseCookie access = cookieFactory.clearAccessTokenCookie();
        ResponseCookie refresh = cookieFactory.clearRefreshTokenCookie();

        resp.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse resp) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RefreshTokenResponse.invalidToken());
        }

        RefreshCommand cmd = new RefreshCommand(refreshToken, userId);
        RefreshResult result = refreshTokenUseCase.refresh(cmd);

        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RefreshTokenResponse.failed(result));
        }

        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(result.accessToken());
        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(result.refreshToken());

        resp.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        resp.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        RefreshTokenResponse response = RefreshTokenResponse.from(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

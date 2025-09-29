package hello.pet.authservice.application.service;

import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import hello.pet.authservice.adapter.out.security.JwtTokenProvider;
import hello.pet.authservice.adapter.out.security.TokenHash;
import hello.pet.authservice.application.exception.LoginCredentialException;
import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.LogoutUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.in.command.LogoutCommand;
import hello.pet.authservice.application.port.out.AuthPort;
import hello.pet.authservice.application.port.out.RefreshTokenRepository;
import hello.pet.authservice.application.port.out.query.LoginQuery;
import hello.pet.authservice.application.port.out.result.LoginResult;
import hello.pet.authservice.application.port.out.result.LogoutResult;
import hello.pet.authservice.domain.entity.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase, LogoutUseCase{

    private final AuthPort authPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Override
    @Transactional
    public LoginResult login(LoginCommand cmd) {
        LoginValidationResponse res = authPort.validationRequest(new LoginQuery(cmd.email(), cmd.password()));

        log.info("로그인 시도: email={}, valid={}", res.email(), res.valid());

        if (!res.valid()) {
            throw new LoginCredentialException("이메일 혹은 패스워드가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(res.email(), res.id(), res.role());
        String refreshToken = jwtTokenProvider.generateRefreshToken(res.email(), res.id(), res.role());

        refreshTokenRepository.save(RefreshToken.issue(res.id(), refreshToken, jwtTokenProvider.getJwtRefreshTokenExpirationMs()));

        log.info("로그인 성공: userId={}, accessToken");

        return LoginResult.success(accessToken, refreshToken, tokenPrefix, res.id(), res.nickname(), res.role(), res.profileUrl());
    }

    @Override
    public LogoutResult logout(LogoutCommand cmd) {

        String refreshToken = cmd.refreshToken();
        Long userId = cmd.userId();

        log.info("로그아웃 시도: userId={}, refreshToken", userId);

        try {
            if (userId == null) {
                userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            }

            String hash = TokenHash.sha256(refreshToken);
            refreshTokenRepository.findByUserIdAndTokenHash(userId, hash)
                    .ifPresent(token -> {
                        token.revoked();
                        refreshTokenRepository.save(token);
                    });

            log.info("로그아웃 성공: userId={}", userId);
            return LogoutResult.successLogout();
        } catch (Exception e) {
            return LogoutResult.fail("로그아웃 처리 중 서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

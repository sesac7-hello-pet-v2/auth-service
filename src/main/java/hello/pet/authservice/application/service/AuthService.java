package hello.pet.authservice.application.service;

import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import hello.pet.authservice.adapter.out.security.JwtTokenProvider;
import hello.pet.authservice.adapter.out.security.TokenHash;
import hello.pet.authservice.application.exception.LoginCredentialException;
import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.LogoutUseCase;
import hello.pet.authservice.application.port.in.RefreshTokenUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.in.command.LogoutCommand;
import hello.pet.authservice.application.port.in.command.RefreshCommand;
import hello.pet.authservice.application.port.out.AuthPort;
import hello.pet.authservice.application.port.out.RefreshTokenRepository;
import hello.pet.authservice.application.port.out.query.LoginQuery;
import hello.pet.authservice.application.port.out.result.LoginResult;
import hello.pet.authservice.application.port.out.result.LogoutResult;
import hello.pet.authservice.application.port.out.result.RefreshResult;
import hello.pet.authservice.domain.entity.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase, LogoutUseCase, RefreshTokenUseCase {

    private final AuthPort authPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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

        return LoginResult.success(jwtTokenProvider.getJwtAccessExpirationMs(), accessToken, refreshToken, res.id(), res.nickname(), res.role(), res.profileUrl());
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

    @Override
    @Transactional
    public RefreshResult refresh(RefreshCommand cmd) {
        String refreshToken = cmd.refreshToken();
        Long userId = cmd.userId();

        log.info("토큰 갱신 시도: userId={}", userId);

        try {
            // 토큰에서 사용자 정보 추출 (userId가 없을 경우)
            if (userId == null) {
                userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            }

            // Refresh Token 유효성 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("Invalid refresh token for user: {}", userId);
                return RefreshResult.fail("유효하지 않은 리프레시 토큰입니다.");
            }

            // DB에서 토큰 검증
            String tokenHash = TokenHash.sha256(refreshToken);
            RefreshToken storedToken = refreshTokenRepository.findByUserIdAndTokenHash(userId, tokenHash)
                    .orElse(null);

            if (storedToken == null) {
                log.warn("Refresh token not found in database: userId={}", userId);
                return RefreshResult.fail("존재하지 않는 리프레시 토큰입니다.");
            }

            if (storedToken.isRevoked()) {
                log.warn("Refresh token is revoked: userId={}", userId);
                return RefreshResult.fail("취소된 리프레시 토큰입니다.");
            }

            if (storedToken.getExpiresAt().isBefore(java.time.Instant.now())) {
                log.warn("Refresh token is expired: userId={}", userId);
                return RefreshResult.fail("만료된 리프레시 토큰입니다.");
            }

            // 토큰에서 사용자 정보 추출
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            String role = jwtTokenProvider.getRoleFromToken(refreshToken);

            // 새로운 토큰 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(email, userId, role);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(email, userId, role);

            // 기존 토큰 무효화
            storedToken.revoked();
            refreshTokenRepository.save(storedToken);

            // 새로운 리프레시 토큰 저장
            RefreshToken newToken = RefreshToken.issue(userId, newRefreshToken, jwtTokenProvider.getJwtRefreshTokenExpirationMs());
            refreshTokenRepository.save(newToken);

            log.info("토큰 갱신 성공: userId={}", userId);
            return RefreshResult.success(jwtTokenProvider.getJwtAccessExpirationMs(), newAccessToken, newRefreshToken);

        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생: userId={}, error={}", userId, e.getMessage(), e);
            return RefreshResult.fail("토큰 갱신 중 서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

package hello.pet.authservice.application.port.out;

import hello.pet.authservice.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByUserIdAndTokenHash(Long userId, String hash);
}

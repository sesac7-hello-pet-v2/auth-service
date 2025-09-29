package hello.pet.authservice.adapter.out.persistence;

import hello.pet.authservice.application.port.out.RefreshTokenRepository;
import hello.pet.authservice.domain.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByUserIdAndTokenHash(Long userId, String hash) {
        return refreshTokenJpaRepository.findByUserIdAndTokenHash(userId, hash);
    }
}

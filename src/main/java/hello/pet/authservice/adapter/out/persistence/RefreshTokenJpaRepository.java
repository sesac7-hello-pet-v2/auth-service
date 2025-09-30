package hello.pet.authservice.adapter.out.persistence;

import hello.pet.authservice.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserIdAndTokenHash(Long userId, String tokenHash);
}

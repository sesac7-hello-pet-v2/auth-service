package hello.pet.authservice.adapter.out.persistence;

import hello.pet.authservice.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
}

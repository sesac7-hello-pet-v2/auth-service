package hello.pet.authservice.application.port.out;

import hello.pet.authservice.domain.entity.RefreshToken;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
}

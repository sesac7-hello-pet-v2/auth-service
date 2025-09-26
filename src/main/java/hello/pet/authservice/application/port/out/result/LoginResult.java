package hello.pet.authservice.application.port.out.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String tokenType,  // "Bearer"
        Long expiresIn,     // 3600 (초 단위)
        Long userId,
        String nickname) {
}

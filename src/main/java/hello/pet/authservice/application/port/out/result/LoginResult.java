package hello.pet.authservice.application.port.out.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        String tokenType,  // "Bearer"
        Long userId,
        String nickname,
        String role,
        String profileUrl
) {
    public static LoginResult success(String accessToken, String refreshToken, String tokenPrefix, Long id, String nickname, String role, String userProfileUrl) {
        return new LoginResult(accessToken, refreshToken, tokenPrefix, id, nickname, role, userProfileUrl);
    }
}

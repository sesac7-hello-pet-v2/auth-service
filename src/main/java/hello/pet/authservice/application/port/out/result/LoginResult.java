package hello.pet.authservice.application.port.out.result;

public record LoginResult(
        String accessToken,
        String refreshToken,
        Long userId,
        String nickname,
        String role,
        String profileUrl
) {
    public static LoginResult success(String accessToken, String refreshToken, Long id, String nickname, String role, String userProfileUrl) {
        return new LoginResult(accessToken, refreshToken, id, nickname, role, userProfileUrl);
    }
}

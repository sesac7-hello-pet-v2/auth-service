package hello.pet.authservice.application.port.out.result;

public record RefreshResult(
        boolean success,
        String message,
        String accessToken,
        String refreshToken
) {
    public static RefreshResult success(String accessToken, String refreshToken) {
        return new RefreshResult(true, "토큰이 성공적으로 갱신되었습니다.", accessToken, refreshToken);
    }

    public static RefreshResult fail(String message) {
        return new RefreshResult(false, message, null, null);
    }
}
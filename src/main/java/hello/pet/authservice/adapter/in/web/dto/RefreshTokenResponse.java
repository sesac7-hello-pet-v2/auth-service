package hello.pet.authservice.adapter.in.web.dto;

import hello.pet.authservice.application.port.out.result.RefreshResult;

public record RefreshTokenResponse(
        int expireIn,
        boolean success,
        String message
) {
    public static RefreshTokenResponse from(RefreshResult result) {
        return new RefreshTokenResponse(result.expireIn(), result.success(), result.message());
    }

    public static RefreshTokenResponse invalidToken() {
        return new RefreshTokenResponse(0,false, "리프레시 토큰이 없습니다.");
    }

    public static RefreshTokenResponse failed(RefreshResult result) {
        return new RefreshTokenResponse(result.expireIn(), result.success(), result.message());
    }
}
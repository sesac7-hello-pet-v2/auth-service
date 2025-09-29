package hello.pet.authservice.adapter.in.web.dto;

import hello.pet.authservice.application.port.out.result.RefreshResult;

public record RefreshTokenResponse(
        boolean success,
        String message
) {
    public static RefreshTokenResponse from(RefreshResult result) {
        return new RefreshTokenResponse(result.success(), result.message());
    }
}
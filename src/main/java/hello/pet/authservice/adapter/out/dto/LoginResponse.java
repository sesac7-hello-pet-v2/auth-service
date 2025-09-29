package hello.pet.authservice.adapter.out.dto;

import hello.pet.authservice.application.port.out.result.LoginResult;

public record LoginResponse(Long id, String nickname, String role, String profileUrl) {
    public static LoginResponse from(LoginResult res) {
        return new LoginResponse(res.userId(), res.nickname(), res.role(), res.profileUrl());
    }
}

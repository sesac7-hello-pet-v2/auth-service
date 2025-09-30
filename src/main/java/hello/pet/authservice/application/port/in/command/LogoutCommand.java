package hello.pet.authservice.application.port.in.command;

import hello.pet.authservice.adapter.out.security.TokenHash;

public record LogoutCommand(Long userId, String refreshToken) {
}

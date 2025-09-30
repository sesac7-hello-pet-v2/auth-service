package hello.pet.authservice.application.port.in.command;

public record RefreshCommand(String refreshToken, Long userId) {
}
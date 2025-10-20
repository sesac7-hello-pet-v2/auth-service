package hello.pet.authservice.adapter.in.web.dto;

import hello.pet.authservice.application.port.in.command.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email은 필수입니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
        ) {
    public LoginCommand toCommand(LoginRequest req) {
        return new LoginCommand(req.email, req.password);
    }
}

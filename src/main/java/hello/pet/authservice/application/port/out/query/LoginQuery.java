package hello.pet.authservice.application.port.out.query;

import hello.pet.authservice.adapter.out.dto.LoginValidationRequest;

public record LoginQuery(String email,
                         String password) {
    public LoginValidationRequest toRequest() {
        return new LoginValidationRequest(email, password);
    }
}

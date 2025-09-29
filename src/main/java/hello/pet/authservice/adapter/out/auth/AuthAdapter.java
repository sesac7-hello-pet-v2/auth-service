package hello.pet.authservice.adapter.out.auth;

import hello.pet.authservice.adapter.out.AuthClient;
import hello.pet.authservice.adapter.out.dto.LoginValidationRequest;
import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import hello.pet.authservice.application.port.out.AuthPort;
import hello.pet.authservice.application.port.out.query.LoginQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {

    private final AuthClient authClient;

    @Override
    public LoginValidationResponse validationRequest(LoginQuery query) {
        LoginValidationRequest req = query.toRequest();
        return authClient.validationRequest(req);
    }
}

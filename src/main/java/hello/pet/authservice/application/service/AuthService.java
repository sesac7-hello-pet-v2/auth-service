package hello.pet.authservice.application.service;

import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.out.AuthPort;
import hello.pet.authservice.application.port.out.query.LoginQuery;
import hello.pet.authservice.application.port.out.result.LoginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    private final AuthPort authPort;

    @Override
    public LoginResult login(LoginCommand cmd) {
        LoginQuery query = new LoginQuery(cmd.email(), cmd.password());
        authPort.validationRequest(query);

        return null;
    }
}

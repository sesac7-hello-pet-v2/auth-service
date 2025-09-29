package hello.pet.authservice.application.port.in;

import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.out.result.LoginResult;

public interface LoginUseCase {
    LoginResult login(LoginCommand cmd);
}

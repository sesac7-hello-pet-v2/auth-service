package hello.pet.authservice.application.port.in;

import hello.pet.authservice.application.port.in.command.LogoutCommand;
import hello.pet.authservice.application.port.out.result.LogoutResult;

public interface LogoutUseCase {
    LogoutResult logout(LogoutCommand cmd);
}

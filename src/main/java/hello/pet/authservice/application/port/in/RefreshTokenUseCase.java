package hello.pet.authservice.application.port.in;

import hello.pet.authservice.application.port.in.command.RefreshCommand;
import hello.pet.authservice.application.port.out.result.RefreshResult;

public interface RefreshTokenUseCase {
    RefreshResult refresh(RefreshCommand command);
}
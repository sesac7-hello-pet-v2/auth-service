package hello.pet.authservice.application.port.out;

import hello.pet.authservice.application.port.out.query.LoginQuery;

public interface AuthPort {
    void validationRequest(LoginQuery query);
}

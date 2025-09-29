package hello.pet.authservice.application.port.out;

import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import hello.pet.authservice.application.port.out.query.LoginQuery;

public interface AuthPort {
    LoginValidationResponse validationRequest(LoginQuery query);
}

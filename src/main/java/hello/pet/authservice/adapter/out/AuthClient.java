package hello.pet.authservice.adapter.out;

import hello.pet.authservice.adapter.out.dto.LoginValidationRequest;
import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "auth-client", url = "${AUTH_SERVICE_URL:http://localhost:8082}")
public interface AuthClient {

    @PostMapping("/internal/v1/users/validate")
    LoginValidationResponse validationRequest(LoginValidationRequest req);
}

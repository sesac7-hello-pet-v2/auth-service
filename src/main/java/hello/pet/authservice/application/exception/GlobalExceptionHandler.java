package hello.pet.authservice.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginCredentialException.class)
    public ResponseEntity<Map<String, Object>> handleLogin(LoginCredentialException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("code", "UNAUTHORIZED", "message", e.getMessage()));
    }

    // 필요시 추가 핸들러들...
}


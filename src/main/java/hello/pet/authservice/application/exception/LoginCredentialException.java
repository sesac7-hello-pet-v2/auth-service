package hello.pet.authservice.application.exception;

public class LoginCredentialException extends RuntimeException {
    public LoginCredentialException(String message) {
        super(message);
    }
}
